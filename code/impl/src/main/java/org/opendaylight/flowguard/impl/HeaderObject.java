/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;

import java.util.List;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.vlan.match.fields.VlanId;

import com.google.common.net.InetAddresses;


public class HeaderObject {
    public long dl_src = 0;
    public long dl_dst = 0;
    public short dl_type = 0;
    public Ipv4Prefix nw_src_prefix = 0;
    public int nw_src_maskbits = 0;
    public Ipv4Prefix nw_dst_prefix = 0;
    public int nw_dst_maskbits = 0;
    public VlanId vlan = -1;
	public List<HeaderObject> diff;

	public HeaderObject get_ho(){
		HeaderObject temp = new HeaderObject();
		temp.dl_src = this.dl_src;
		temp.dl_dst = this.dl_dst;
		temp.dl_type = this.dl_type;
		temp.nw_src_prefix = this.nw_src_prefix;
		temp.nw_src_maskbits = this.nw_src_maskbits;
		temp.nw_dst_prefix = this.nw_dst_prefix;
		temp.nw_dst_maskbits = this.nw_dst_maskbits;
		temp.vlan = this.vlan;
		temp.diff = this.diff;
		return temp;
	}

	public static void printHeaderObject(HeaderObject ho){
		/*
		System.out.println("{dl_src="+HexString.toHexString(ho.dl_dst)+", dl_dst="+HexString.toHexString(ho.dl_dst)+
				", dl_type="+Short.toString(ho.dl_type)+", src_IP="+IPv4.fromIPv4Address(ho.nw_src_prefix)+
				", src_IP_mask="+Integer.toString(ho.nw_src_maskbits)+", dst_IP="+IPv4.fromIPv4Address(ho.nw_dst_prefix)+
				", dst_IP_mask="+Integer.toString(ho.nw_dst_maskbits)+"}");
		*/
		System.out.println("{ vlan = "+Short.toString(ho.vlan)+", src_IP = "+ InetAddresses.fromInteger(ho.nw_src_prefix)+"/"+Integer.toString(ho.nw_src_maskbits)+
				", dst_IP = "+InetAddresses.fromInteger(ho.nw_dst_prefix)+"/"+Integer.toString(ho.nw_dst_maskbits)+" }");
		if(ho.diff != null){
			for(int i = 0; i < ho.diff.size(); i++){
				System.out.println("{ diff["+Integer.toString(i)+"].src_IP = "+InetAddresses.fromInteger(ho.diff.get(i).nw_src_prefix)+
						"/"+Integer.toString(ho.diff.get(i).nw_src_maskbits)+
						", diff["+Integer.toString(i)+"].dst_IP = "+InetAddresses.fromInteger(ho.diff.get(i).nw_dst_prefix)+
						"/"+Integer.toString(ho.diff.get(i).nw_dst_maskbits)+" }");
			}
		}
	}
}
