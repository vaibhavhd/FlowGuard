import requests
import json
import argparse
import sys
import netaddr
import threading
import Queue
import random
import copy
import time


flow_template = {
  "flow": {
    "priority": "50004",
    "idle-timeout": "30",
    "hard-timeout": "30",
    "match": {
      "ethernet-match": {
        "ethernet-type": { "type": "2048" }
      },
      "ipv4-destination": "10.0.0.1/32",
      "ipv4-source": "10.0.0.2/32"
    },
    "id": "1232",
    "table_id": "0",
    "instructions": {
      "instruction": {
        "order": "0",
        "apply-actions": {
          "action": {
            "order": "0",
            "output-action": { "output-node-connector": "openflow:1:2" }
          }
        }
      }
    }
  }
}


cntl = '127.0.0.1'
odl_node_url = '/restconf/config/opendaylight-inventory:nodes/node/'
dev_id = 'openflow:1'
url = 'http://' + cntl + ':8181' + odl_node_url + dev_id + '/table/0'
ses = requests.Session()

                #flow = copy.deepcopy(template)
                #flow["id"] = ip
priority = 1
flowid = 1
ipaddress = '10.0.0.'
hostaddress = '1'
template = copy.deepcopy(flow_template)

for flowid in range(42949670):		 


	template["flow"]["priority"] = str(priority)
	template["flow"]["id"] = "Flows-id-" + str(flowid)

	req_data = json.dumps(template)	
	req = requests.Request('POST', url, headers={'Content-Type': 'application/json'},

	data=req_data, auth=('admin', 'admin'))
	prep = req.prepare()
	priority = priority + 1
	if flowid % 1000 == 0:
		print flowid
	if priority == 65536:
		priority = 1
		hostaddress = str(int(hostaddress) + 1)
		template["flow"]["match"]["ipv4-destination"] = ipaddress + hostaddress + '/32'

	try:
		rsp = ses.send(prep, timeout=5)
	except requests.exceptions.Timeout:
		print rsp.status_code
		print rsp.content
	if rsp.status_code >= 300:
		print rsp.status_code
		print rsp.content
		break

print flowid