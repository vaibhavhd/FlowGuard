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
    public String port;
    
    public TopologyStruct() {
		// TODO Auto-generated constructor stub
	}
    
	public TopologyStruct(String destId, String destPort) {
		this.dpid = destId;
		this.port = destPort;
	}
}