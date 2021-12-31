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

package com.vaadin.flow.server.frontend.scanner.samples;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;

@JsModule("foo.js")
@Theme(themeClass = CustomTheme.class)
public class RouteLayoutComponent implements RouterLayout {
    @Override
    public Element getElement() {
        return null;
    }
}
