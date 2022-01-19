package com.vaadin.flow.uitest.ui.push;

import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.internal.nodefeature.PushConfigurationMap;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.shared.internal.PushConfigurationConstants;
import com.vaadin.flow.shared.ui.Transport;

@Push(transport = Transport.WEBSOCKET)
public class PushLargeDataWebsocketUI extends PushLargeData {

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        getPushConfiguration().setParameter(
            PushConfigurationConstants.FALLBACK_TRANSPORT_KEY, "none");
    }
}
