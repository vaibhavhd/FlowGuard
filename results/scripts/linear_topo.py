#!/usr/bin/python
from mininet.net import Mininet
from mininet.node import UserSwitch, OVSKernelSwitch, Controller
from mininet.topo import Topo
from mininet.log import lg, info
from mininet.util import irange, quietRun, dumpNodeConnections

from mininet.link import TCLink
from functools import partial
from mininet.node import RemoteController
from mininet.cli import CLI


import sys,os
flush = sys.stdout.flush

class LinearTopo (Topo):
    "Linear topology of k switches, with one host per switch."
    def __init__(self, k=3, **opts):
        """Init.
            k: number of switches (and hosts)
            hconf: host configuration options
            lconf: link configuration options"""
        super(LinearTopo, self).__init__(**opts)
        self.k = k
        lastSwitch = None
        for i in irange(1, k):
            host = self.addHost('h%s' % i)
            switch = self.addSwitch('s%s' % i)
            self.addLink( host, switch)
            if lastSwitch:
                self.addLink( switch, lastSwitch)
            lastSwitch = switch


def simpleTest():

    "Create and test a simple network"
    c = RemoteController('c','127.0.0.1',6633)
    topo = LinearTopo(3)
    net = Mininet( topo=topo, switch=OVSKernelSwitch,controller=c, waitConnected=True)
    net.start()
    print "Dumping host connections"
    dumpNodeConnections(net.hosts)
    print "Testing network connectivity"
    net.pingAll()
    CLI(net)
    net.stop()

def clearPreviousTopo():
    os.system('sudo mn -c')

if __name__ == '__main__':
    lg.setLogLevel( 'info' )
    #
    #info( "*** Running linearBandwidthTest", sizes, '\n' )
    #linearBandwidthTest( sizes  )
    clearPreviousTopo()
    simpleTest()
