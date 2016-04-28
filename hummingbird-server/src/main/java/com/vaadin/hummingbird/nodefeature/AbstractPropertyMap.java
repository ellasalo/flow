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
import java.util.stream.Stream;

import com.vaadin.hummingbird.StateNode;

import elemental.json.JsonValue;

/**
 * Abstract class to be used as a parent for node maps which supports setting
 * properties in a map.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractPropertyMap extends NodeMap {

    /**
     * Creates a new element property map for the given node.
     *
     * @param node
     *            the node that the map belongs to
     */
    public AbstractPropertyMap(StateNode node) {
        super(node);
    }

    /**
     * Sets a property to the given value.
     *
     * @param name
     *            the property name
     * @param value
     *            the value, must be a string, a boolean, a double or
     *            <code>null</code>
     * @param emitChange
     *            true to create a change event for the client side
     */
    public void setProperty(String name, Serializable value,
            boolean emitChange) {
        assert name != null;
        assert isValidValueType(value);

        put(name, value, emitChange);
    }

    /**
     * Checks whether there is a property of the given name.
     *
     * @param name
     *            the name of the property
     * @return <code>true</code> if there is a propety with the given name;
     *         <code>false</code> if there is no property
     */
    public boolean hasProperty(String name) {
        return contains(name);
    }

    /**
     * Removes the given property.
     *
     * @param name
     *            the name of the property to remove
     */
    public void removeProperty(String name) {
        super.remove(name);
    }

    /**
     * Removes all properties.
     *
     */
    public void removeAllProperties() {
        super.clear();
    }

    /**
     * Gets the value of the given property.
     *
     * @param name
     *            the name of the property
     * @return the property value; <code>null</code> if there is no property or
     *         if the value is explicitly set to null
     */
    public Object getProperty(String name) {
        return get(name);
    }

    /**
     * Gets the property names.
     *
     * @return a stream containing all the property names that have been set
     */
    public Stream<String> getPropertyNames() {
        return keySet().stream();
    }

    /**
     * Checks if the given value is of a supported type.
     *
     * @param value
     *            the value to check, may be null
     * @return <code>true</code> if the type is supported, <code>false</code>
     *         otherwise
     */
    public static boolean isValidValueType(Serializable value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return true;
        }
        if (value instanceof Boolean) {
            return true;
        }
        if (value instanceof Double) {
            return true;
        }
        if (value instanceof JsonValue) {
            return true;
        }

        return false;
    }

}
