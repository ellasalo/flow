/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.flow.demo;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.flow.testutil.ChromeBrowserTest;

/**
 * Base class for the integration tests of component demos.
 *
 * @since 1.0
 */
public abstract class ComponentDemoTest extends ChromeBrowserTest {
    protected WebElement layout;

    @Override
    protected int getDeploymentPort() {
        return 9998;
    }

    /**
     * Runs before each test.
     */
    @Before
    public void openDemoPageAndCheckForErrors() {
        open();
        waitForElementPresent(By.className("demo-view"));
        layout = findElement(By.className("demo-view"));
        checkLogsForErrors();
    }
}