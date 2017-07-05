#!/usr/bin/python
import json
import requests
import httplib2
import sys
from pathlib import Path
from requests.auth import HTTPBasicAuth

def pushStaticRules(http, filePath):
    #Authetication
    url = 'http://127.0.0.1:8181/restconf/operations/flowguard:add-static-fwrule'
    #filePath = '/home/local/ASUAD/vdixit2/workspace/FlowGuard/results/scripts/FirewallPolicy.txt'
    json_data = { "input": { "filePath": filePath } }
    payload = json.dumps(json_data)

    resp, content = http.request(
      uri = url,
      method = 'POST',
      headers={'Content-Type' : 'application/json'},
      body=payload
    )
    print resp

def startFlowguard(http):
    url = 'http://127.0.0.1:8181/restconf/operations/flowguard:flowguard-control'
    json_data = { "input": {"action": "Enable"} }
    payload = json.dumps(json_data)

    resp, content = http.request(
      uri = url,
      method = 'POST',
      headers={'Content-Type' : 'application/json'},
      body=payload
    )
    print resp

def main():

    if len(sys.argv) != 2:
	print "Usage: ./flowguard.py /path/to/file/with/static/FW/rules"
	sys.exit()
    filePath = sys.argv[1];
    if not ((Path(filePath)).is_file()):
	print "Error: File "+ sys.argv[1] +" does not exist"
	sys.exit();

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')
    #Push the list of Firewall Rules
    pushStaticRules(h, filePath)
    #Enable Flowguard
    startFlowguard(h)

if __name__ == "__main__":
	main()
