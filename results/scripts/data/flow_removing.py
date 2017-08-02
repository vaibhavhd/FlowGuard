flow1 = {
    'switch':"00:00:00:00:00:00:00:02",
    "name":"flow1-1",
    "priority":"0",
    "active":"true",
    "ether-type":"0x800",
    "src-ip":"10.0.0.1/32",
    "dst-ip":"10.0.0.5/32",
    "ingress-port":"1",
    "actions":"set-src-ip=10.0.0.6,set-src-mac=00:00:00:00:00:01,output=3"
    }
flow2 = {
    'switch':"00:00:00:00:00:00:00:02",
    "name":"flow1-2",
    "priority":"0",
    "active":"true",
    "ether-type":"0x806",
    "src-ip":"10.0.0.1/32",
    "dst-ip":"10.0.0.5/32",
    "ingress-port":"1",
    "actions":"set-src-ip=10.0.0.6,set-src-mac=00:00:00:00:00:01,output=3"
    }
flow3 = {
    'switch':"00:00:00:00:00:00:00:01",
    "name":"flow1-3",
    "priority":"0",
    "active":"true",
    "ether-type":"0x800",
    "src-ip":"10.0.0.6/32",
    "dst-ip":"10.0.0.5/32",
    "ingress-port":"1",
    "actions":"set-dst-ip=10.0.0.4,set-dst-mac=00:00:00:00:00:04,output=2"
    }
flow4 = {
    'switch':"00:00:00:00:00:00:00:01",
    "name":"flow1-4",
    "priority":"0",
    "active":"true",
    "ether-type":"0x806",
    "src-ip":"10.0.0.6/32",
    "dst-ip":"10.0.0.5/32",
    "ingress-port":"1",
    "actions":"set-dst-ip=10.0.0.4,set-dst-mac=00:00:00:00:00:04,output=2"
    }
flow5 = {
    'switch':"00:00:00:00:00:00:00:03",
    "name":"flow1-5",
    "priority":"0",
    "active":"true",
    "ether-type":"0x800",
    "src-ip":"10.0.0.4/32",
    "dst-ip":"10.0.0.6/32",
    "ingress-port":"2",
    "actions":"set-src-ip=10.0.0.5,set-src-mac=00:00:00:00:00:04,output=3"
    }
flow6 = {
    'switch':"00:00:00:00:00:00:00:03",
    "name":"flow1-6",
    "priority":"0",
    "active":"true",
    "ether-type":"0x806",
    "src-ip":"10.0.0.4/32",
    "dst-ip":"10.0.0.6/32",
    "ingress-port":"2",
    "actions":"set-src-ip=10.0.0.5,set-src-mac=00:00:00:00:00:04,output=3"
    }
flow7 = {
    'switch':"00:00:00:00:00:00:00:01",
    "name":"flow1-7",
    "priority":"0",
    "active":"true",
    "ether-type":"0x800",
    "src-ip":"10.0.0.5/32",
    "dst-ip":"10.0.0.6/32",
    "ingress-port":"2",
    "actions":"set-dst-ip=10.0.0.1,set-dst-mac=00:00:00:00:00:01,output=1"
    }
flow8 = {
    'switch':"00:00:00:00:00:00:00:01",
    "name":"flow1-8",
    "priority":"0",
    "active":"true",
    "ether-type":"0x806",
    "src-ip":"10.0.0.5/32",
    "dst-ip":"10.0.0.6/32",
    "ingress-port":"2",
    "actions":"set-dst-ip=10.0.0.1,set-dst-mac=00:00:00:00:00:01,output=1"
    }
