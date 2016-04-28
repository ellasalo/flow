/*
 * Copyright 2000-2016 Vaadin Ltd.
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

package com.vaadin.hummingbird.nodefeature;

import java.io.Serializable;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.dom.ElementUtil;
import com.vaadin.hummingbird.dom.Style;
import com.vaadin.hummingbird.dom.impl.BasicElementStyle;

/**
 * Map for element style values.
 *
 * @author Vaadin Ltd
 */
public class ElementStylePropertyMap extends AbstractPropertyMap {

    /**
     * Creates a new element style map for the given node.
     *
     * @param node
     *            the node that the map belongs to
     */
    public ElementStylePropertyMap(StateNode node) {
        super(node);
    }

    @Override
    public void setProperty(String name, Serializable value,
            boolean emitChange) {
        assert value instanceof String;
        assert ElementUtil.isValidStylePropertyValue((String) value);
        super.setProperty(name, value, emitChange);
    }

    /**
     * Returns a style instance for managing element inline styles.
     *
     * @return a Style instance connected to this map
     */
    public Style getStyle() {
        return new BasicElementStyle(this);
    }

}
