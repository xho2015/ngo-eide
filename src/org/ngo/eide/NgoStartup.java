package org.ngo.eide;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.eclipse.ui.IStartup;
import org.ngo.eide.handler.NgoIDE;
import org.ngo.ether.endpoint.EndpointHandler;
import org.ngo.ether.endpoint.EndpointSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NgoStartup implements IStartup {
	private final static Logger LOGGER = LoggerFactory.getLogger(NgoStartup.class);

	private EndpointSupport client;

    private EndpointHandler handler;
    
	@Override
	public void earlyStartup() {

		int port = Integer.valueOf(System.getProperty("ngo.bridge.port","60001"));
        String host = System.getProperty("ngo.bridge.host","127.0.0.1");
        
		SocketAddress address = new InetSocketAddress(host,port);
        int ngoID = 9;
        
		handler= new EndpointHandler(NgoIDE.instance, (short)ngoID);
		client = new EndpointSupport("IDE", handler);
		
        if (!client.connect(address, false)) {
        	LOGGER.error(String.format("failed to connect to %s", address));
        } else {
        	LOGGER.info(String.format("successfully connected to %s", address));        	
        }
	}

}
