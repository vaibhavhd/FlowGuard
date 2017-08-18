/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;// rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.IpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatchBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchUtils {
  private static final Logger logger = LoggerFactory.getLogger(MatchUtils.class);
  public static final short ICMP_SHORT = 1;
  public static final short TCP_SHORT = 6;
  public static final short UDP_SHORT = 17;
  public static final String TCP = "tcp";
  public static final String UDP = "udp";
  public static final int TCP_SYN = 0x0002;
  public static final long IPV4_LONG = (long) 0x800;
  public static final long LLDP_LONG = (long) 0x88CC;
  public static final long VLANTAGGED_LONG = (long) 0x8100;
  public static final long MPLSUCAST_LONG = (long) 0x8847;

  public static MatchBuilder createDstL3IPv4Match(MatchBuilder matchBuilder, Ipv4Prefix dstIp, Ipv4Prefix srcIp) {

    EthernetMatchBuilder eth = new EthernetMatchBuilder();
    EthernetTypeBuilder ethTypeBuilder = new EthernetTypeBuilder();
    ethTypeBuilder.setType(new EtherType(IPV4_LONG));
    eth.setEthernetType(ethTypeBuilder.build());
    matchBuilder.setEthernetMatch(eth.build());
    
    Ipv4MatchBuilder ipv4match = new Ipv4MatchBuilder();
    ipv4match.setIpv4Destination(dstIp);
    ipv4match.setIpv4Source(srcIp);
    matchBuilder.setLayer3Match(ipv4match.build());

    return matchBuilder;

  }

  public static MatchBuilder createSetDstTcpMatch(MatchBuilder matchBuilder, PortNumber tcpDstPort) {

    EthernetMatchBuilder ethType = new EthernetMatchBuilder();
    EthernetTypeBuilder ethTypeBuilder = new EthernetTypeBuilder();
    ethTypeBuilder.setType(new EtherType(IPV4_LONG));
    ethType.setEthernetType(ethTypeBuilder.build());
    matchBuilder.setEthernetMatch(ethType.build());

    IpMatchBuilder ipmatch = new IpMatchBuilder();
    ipmatch.setIpProtocol((short) 6);
    matchBuilder.setIpMatch(ipmatch.build());

    TcpMatchBuilder tcpmatch = new TcpMatchBuilder();
    tcpmatch.setTcpDestinationPort(tcpDstPort);
    matchBuilder.setLayer4Match(tcpmatch.build());

    return matchBuilder;
  }


}
