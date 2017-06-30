#!/usr/bin/python
import json
import requests
from requests.auth import HTTPBasicAuth
import httplib2

def pushStaticRules(http):
    #Authetication
    url = 'http://127.0.0.1:8181/restconf/operations/flowguard:add-static-fwrule'
    filePath = '/home/local/ASUAD/vdixit2/workspace/FlowGuard/results/scripts/FirewallPolicy.txt'
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

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    #Push the list of Firewall Rules
    pushStaticRules(h)
    #Enable Flowguard
    startFlowguard(h)

if __name__ == "__main__":
	main()
