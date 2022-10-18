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
package com.vaadin.flow.data.provider;

import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract data provider implementation which takes care of refreshing data
 * from the underlying data provider.
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 *
 * @author Vaadin Ltd
 * @since 1.0
 *
 */
public abstract class AbstractDataProvider<T, F> implements DataProvider<T, F> {

    private HashMap<Class<?>, List<DataListenerWrapper>> listeners = new HashMap<>();

    private static class DataListenerWrapper implements Serializable {
        private final SerializableConsumer<?> listener;
        private Registration registration;

        public DataListenerWrapper(SerializableConsumer<?> listener) {
            this.listener = listener;
        }
    }

    @Override
    public Registration addDataProviderListener(
            DataProviderListener<T> listener) {
        // Using an anonymous class instead of lambda or method reference to
        // prevent potential self reference serialization issues when clients
        // hold a reference to the Registration instance returned by this method
        SerializableConsumer<DataChangeEvent> consumer = new SerializableConsumer<DataChangeEvent>() {
            @Override
            public void accept(DataChangeEvent dataChangeEvent) {
                listener.onDataChange(dataChangeEvent);
            }
        };
        return addListener(DataChangeEvent.class, consumer);
    }

    @Override
    public void refreshAll() {
        fireEvent(new DataChangeEvent<>(this));
    }

    @Override
    public void refreshItem(T item, boolean refreshChildren) {
        fireEvent(new DataRefreshEvent<>(this, item, refreshChildren));
    }

    @Override
    public void refreshItem(T item) {
        fireEvent(new DataRefreshEvent<>(this, item));
    }

    /**
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     *
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param method
     *            the consumer to receive the event.
     * @param <E>
     *            the event type
     * @return a registration for the listener
     */
    protected <E> Registration addListener(Class<E> eventType,
            SerializableConsumer<E> method) {
        List<DataListenerWrapper> list = listeners.computeIfAbsent(eventType,
                key -> new ArrayList<>());

        DataListenerWrapper wrapper = new DataListenerWrapper(method);

        final Registration registration = Registration.addAndRemove(list,
                wrapper);

        // Using an anonymous class instead of lambda or method reference to
        // prevent potential self reference serialization issues when clients
        // hold a reference to the Registration instance returned by this method
        wrapper.registration = new Registration() {
            @Override
            public void remove() {
                registration.remove();
            }
        };

        return wrapper.registration;
    }

    /**
     * Sends the event to all listeners.
     *
     * @param event
     *            the Event to be sent to all listeners.
     */
    protected void fireEvent(EventObject event) {
        listeners.entrySet().stream().filter(
                entry -> entry.getKey().isAssignableFrom(event.getClass()))
                .forEach(entry -> new ArrayList<>(entry.getValue()).forEach(
                        wrapper -> fireEventForListener(event, wrapper)));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void fireEventForListener(EventObject event,
            DataListenerWrapper wrapper) {
        if (event instanceof DataChangeEvent<?>) {
            DataChangeEvent<?> dataEvent = (DataChangeEvent<?>) event;

            dataEvent
                    .setUnregisterListenerCommand(wrapper.registration::remove);
            Consumer consumer = wrapper.listener;
            try {
                consumer.accept(event);
            } finally {
                dataEvent.setUnregisterListenerCommand(null);
            }
        }
    }
}
