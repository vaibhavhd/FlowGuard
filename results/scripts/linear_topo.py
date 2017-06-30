#!/usr/bin/python
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import irange,dumpNodeConnections
from mininet.log import setLogLevel
from mininet.node import Controller, RemoteController, OVSController
from mininet.cli import CLI

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
    topo = LinearTopo(k=3)
    net = Mininet(topo)
    net.addController( 'c0', controller=RemoteController, ip='127.0.0.1', port=6633 )
    net.start()
    print "Dumping host connections"
    dumpNodeConnections(net.hosts)
    print "Testing network connectivity"
    net.pingAll()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    # Tell mininet to print useful information
    setLogLevel('info')
    simpleTest()
