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
from mininet.node import Node
import sys,os

flush = sys.stdout.flush

class LinuxRouter( Node ):
    "A Node with IP forwarding enabled."

    def config( self, **params ):
        super( LinuxRouter, self).config( **params )
        # Enable forwarding on the router
        self.cmd( 'sysctl net.ipv4.ip_forward=1' )

    def terminate( self ):
        self.cmd( 'sysctl net.ipv4.ip_forward=0' )
        super( LinuxRouter, self ).terminate()

class MyTopo(Topo):
    def __init__( self ):
        Topo.__init__(self)

        defaultIP = '192.168.1.1/24'
        router = self.addNode( 'r0', cls=LinuxRouter, ip=defaultIP )
        s1, s2, s3 = [ self.addSwitch( s ) for s in ( 's1', 's2', 's3' ) ]

        self.addLink( s1, router, intfName2='r0-eth1',
                      params2={ 'ip' : defaultIP } )  # for clarity
        self.addLink( s2, router, intfName2='r0-eth2',
                      params2={ 'ip' : '172.16.0.1/12' } )
        self.addLink( s3, router, intfName2='r0-eth3',
                      params2={ 'ip' : '10.0.0.1/8' } )

        #Control Network
        plc1=self.addHost('plc1', ip='192.168.1.2/24', defaultRoute='via 192.168.1.1')
        plc2=self.addHost('plc2', ip='192.168.1.3/24', defaultRoute='via 192.168.1.1')
        hmi=self.addHost('hmi', ip='192.168.1.4/24', defaultRoute='via 192.168.1.1')
        controlServer=self.addHost('conSer',ip='192.168.1.5/24', defaultRoute='via 192.168.1.1')

        ##Add link for control network
        self.addLink(plc1,s1)
        self.addLink(plc2,s1)
        self.addLink(hmi,s1)
        self.addLink(controlServer,s1)
        

        #DMZ Network
        dataHistorian=self.addHost('dataHist',ip='172.16.0.2/12',defaultRoute='via 172.16.0.1')
        dataServer=self.addHost('dataSer',ip='172.16.0.3/12', defaultRoute='via 172.16.0.1')

        ###Add Link DMZ network
        self.addLink(s1,s2)
        self.addLink(dataHistorian,s2)
        self.addLink(dataServer,s2)
        

        #Corporate Network
        workstations=self.addHost('workCom',ip='10.0.0.2/8',
                           defaultRoute='via 10.0.0.1')
        printer=self.addHost('printer',ip='10.0.0.3/8',
                           defaultRoute='via 10.0.0.1')
        applicationServer=self.addHost('appServer',ip='10.0.0.4/8',
                           defaultRoute='via 10.0.0.1')
        #s3=self.addSwitch('cor')
        ### Add link Corporate Network
        self.addLink(s2,s3)
        self.addLink(workstations,s3)
        self.addLink(printer,s3)
        self.addLink(applicationServer,s3)


def simpleTest():

    "Create and test a simple network"
    c = RemoteController('c','127.0.0.1',6633)
    #topo = LinearTopo(3)
    topo=MyTopo()
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
