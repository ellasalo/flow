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

package com.vaadin.flow.navigate;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "special åäö $'´`")
public class SpecialCharactersView extends Span {

    public SpecialCharactersView() {
        setId("special-view");
        Div div = new Div();
        div.setId("title");
        div.setText("This is the special view");
        add(div);
        RouterLink helloLink = new RouterLink("Hello world view",
                HelloWorldView.class);
        helloLink.setId("navigate-hello");
        add(helloLink);
    }

}
