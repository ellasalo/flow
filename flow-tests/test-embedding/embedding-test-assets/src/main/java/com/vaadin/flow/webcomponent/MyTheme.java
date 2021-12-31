/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.flow.webcomponent;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.theme.AbstractTheme;

import com.vaadin.flow.uitest.ui.dependencies.TestVersion;

@NpmPackage(value = "@vaadin/vaadin-themable-mixin", version = TestVersion.VAADIN)
public class MyTheme implements AbstractTheme {

    @Override
    public String getBaseUrl() {
        return "src/";
    }

    @Override
    public String getThemeUrl() {
        return "theme/";
    }
}
