/*
 * Copyright 2000-2023 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.server.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.ExecutionFailedException;
import com.vaadin.flow.server.frontend.scanner.ClassFinder;
import com.vaadin.flow.server.frontend.scanner.FrontendDependenciesScanner;
import com.vaadin.flow.shared.util.SharedUtil;

/**
 * Compiles the dev mode bundle if it is out of date.
 * <p>
 * Only used when running in dev mode without a dev server.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 */
public class TaskRunDevBundleBuild implements FallibleCommand {

    //@formatter:off
    public static final String README =
            "This directory is automatically generated by Vaadin and contains the pre-compiled \n" +
            "frontend files/resources for your project (frontend development bundle).\n\n" +
            "It should be added to Version Control System and committed, so that other developers\n" +
            "do not have to compile it again.\n\n" +
            "Frontend development bundle is automatically updated when needed:\n" +
            "- an npm/pnpm package is added with @NpmPackage or directly into package.json\n" +
            "- CSS, JavaScript or TypeScript files are added with @CssImport, @JsModule or @JavaScript\n" +
            "- Vaadin add-on with front-end customizations is added\n" +
            "- Custom theme imports/assets added into 'theme.json' file\n" +
            "- Exported web component is added.\n\n" +
            "If your project development needs a hot deployment of the frontend changes, \n" +
            "you can switch Flow to use Vite development server (default in Vaadin 23.3 and earlier versions):\n" +
            "- set `vaadin.frontend.hotdeploy=true` in `application.properties`\n" +
            "- configure `vaadin-maven-plugin`:\n" +
            "```\n" +
            "   <configuration>\n" +
            "       <frontendHotdeploy>true</frontendHotdeploy>\n" +
            "   </configuration>\n" +
            "```\n" +
            "- configure `jetty-maven-plugin`:\n" +
            "```\n" +
            "   <configuration>\n" +
            "       <systemProperties>\n" +
            "           <vaadin.frontend.hotdeploy>true</vaadin.frontend.hotdeploy>\n" +
            "       </systemProperties>\n" +
            "   </configuration>\n" +
            "```\n\n" +
            "Read more [about Vaadin development mode](https://vaadin.com/docs/next/configuration/development-mode/#pre-compiled-front-end-bundle-for-faster-start-up).";
    //@formatter:on

    public static final String README_NOT_CREATED = "Failed to create a README file in "
            + Constants.DEV_BUNDLE_LOCATION;

    private final Options options;

    /**
     * Create an instance of the command.
     *
     * @param options
     *            the task options
     */
    TaskRunDevBundleBuild(Options options) {
        this.options = options;
    }

    @Override
    public void execute() throws ExecutionFailedException {
        getLogger().info(
                "Creating a new development mode bundle. This can take a while but will only run when the project setup is changed, addons are added or frontend files are modified");

        runFrontendBuildTool("Vite", "vite/bin/vite.js", Collections.emptyMap(),
                "build");

        addReadme();
    }

    public static boolean needsBuild(Options options,
            FrontendDependenciesScanner frontendDependencies,
            ClassFinder finder) {
        getLogger()
                .info("Checking if a development mode bundle build is needed");

        try {
            boolean needsBuild = BundleValidationUtil.needsBundleBuild(options,
                    frontendDependencies, finder);
            if (needsBuild) {
                getLogger().info("A development mode bundle build is needed");
            } else {
                getLogger()
                        .info("A development mode bundle build is not needed");
            }
            return needsBuild;
        } catch (Exception e) {
            getLogger().error(
                    "Error when checking if a development bundle build is needed",
                    e);
            return true;
        }
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(TaskRunDevBundleBuild.class);
    }

    private void runFrontendBuildTool(String toolName, String executable,
            Map<String, String> environment, String... params)
            throws ExecutionFailedException {
        Logger logger = getLogger();

        FrontendToolsSettings settings = new FrontendToolsSettings(
                options.getNpmFolder().getAbsolutePath(),
                () -> FrontendUtils.getVaadinHomeDirectory().getAbsolutePath());
        settings.setNodeDownloadRoot(options.getNodeDownloadRoot());
        settings.setForceAlternativeNode(options.isRequireHomeNodeExec());
        settings.setUseGlobalPnpm(options.isUseGlobalPnpm());
        settings.setAutoUpdate(options.isNodeAutoUpdate());
        settings.setNodeVersion(options.getNodeVersion());
        FrontendTools frontendTools = new FrontendTools(settings);

        File buildExecutable = new File(options.getNpmFolder(),
                "node_modules/" + executable);
        if (!buildExecutable.isFile()) {
            throw new IllegalStateException(String.format(
                    "Unable to locate %s executable by path '%s'. Double"
                            + " check that the plugin is executed correctly",
                    toolName, buildExecutable.getAbsolutePath()));
        }

        String nodePath;
        if (options.isRequireHomeNodeExec()) {
            nodePath = frontendTools.forceAlternativeNodeExecutable();
        } else {
            nodePath = frontendTools.getNodeExecutable();
        }

        List<String> command = new ArrayList<>();
        command.add(nodePath);
        command.add(buildExecutable.getAbsolutePath());
        command.addAll(Arrays.asList(params));

        String commandString = command.stream()
                .collect(Collectors.joining(" "));

        ProcessBuilder builder = FrontendUtils.createProcessBuilder(command);
        builder.environment().put("devBundle", "true");
        builder.environment().put("NODE_ENV", "development");

        Process process = null;
        try {
            builder.directory(options.getNpmFolder());
            builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            builder.redirectErrorStream(true);

            process = builder.start();

            // This will allow to destroy the process which does IO regardless
            // whether it's executed in the same thread or another (may be
            // daemon) thread
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(process::destroyForcibly));

            logger.debug("Output of `{}`:", commandString);
            StringBuilder toolOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(),
                            StandardCharsets.UTF_8))) {
                String stdoutLine;
                while ((stdoutLine = reader.readLine()) != null) {
                    logger.debug(stdoutLine);
                    toolOutput.append(stdoutLine)
                            .append(System.lineSeparator());
                }
            }

            int errorCode = process.waitFor();

            if (errorCode != 0) {
                logger.error("Command `{}` failed:\n{}", commandString,
                        toolOutput);
                throw new ExecutionFailedException(
                        SharedUtil.capitalize(toolName)
                                + " build exited with a non zero status");
            } else {
                logger.info("Development frontend bundle built");
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Error when running `{}`", commandString, e);
            if (e instanceof InterruptedException) {
                // Restore interrupted state
                Thread.currentThread().interrupt();
            }
            throw new ExecutionFailedException(
                    "Command '" + commandString + "' failed to finish", e);
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }

    private void addReadme() {
        File devBundleFolder = new File(options.getNpmFolder(),
                Constants.DEV_BUNDLE_LOCATION);
        assert devBundleFolder.exists();

        try {
            File readme = new File(devBundleFolder, "README.md");
            boolean created = readme.createNewFile();
            if (created) {
                FileUtils.writeStringToFile(readme, README,
                        StandardCharsets.UTF_8);
            } else {
                getLogger().warn(README_NOT_CREATED);
            }
        } catch (Exception e) {
            getLogger().error(README_NOT_CREATED, e);
        }
    }
}
