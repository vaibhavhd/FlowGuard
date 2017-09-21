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

    public TopologyStruct() {
		// TODO Auto-generated constructor stub
	}

	public TopologyStruct(String destId, int destPort) {
		this.dpid = destId;
		this.port = destPort;
	}
	public static int getPortfromURI(String URI) {
		int port = Integer.valueOf(
				URI.substring(URI.lastIndexOf(':') + 1 ));
		return port;
	}
}