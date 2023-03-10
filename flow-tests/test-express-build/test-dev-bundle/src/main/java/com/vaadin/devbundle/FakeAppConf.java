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
package com.vaadin.devbundle;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Theme("vaadin-dev-bundle")
@PWA(name = "vaadin-dev-bundle", shortName = "vaadin-dev-bundle")
@JsModule("@polymer/paper-input/paper-input.js")
@JsModule("@polymer/paper-checkbox/paper-checkbox.js")
@NpmPackage(value = "@polymer/paper-input", version = "3.0.2")
@NpmPackage(value = "@polymer/paper-checkbox", version = "3.0.1")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class FakeAppConf implements AppShellConfigurator {

}