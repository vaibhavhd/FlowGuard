/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;

public class TopologyStruct {
	public String dpid;
    public int port;
    public int hostIP;
    public String macAddress;

    public TopologyStruct() {
		// TODO Auto-generated constructor stub
	}

	public TopologyStruct(String destId, int destPort) {
		this.dpid = destId;
		this.port = destPort;
	}
	public static int getPortfromURI(String URI) {
	    int port = 0;
	    if(URI.substring(URI.lastIndexOf(':') + 1).equals("LOCAL"))
	        port = 0;
	    else {
    	    try {
        		port = Integer.valueOf(
        				URI.substring(URI.lastIndexOf(':') + 1 ));
    	    }
    	    catch (NumberFormatException exp) {
    	       System.out.println("String Input port found!!");
    	    }
	    }
	    return port;
	}
}