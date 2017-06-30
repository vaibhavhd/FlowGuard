#!/usr/bin/python
import json
import requests
from requests.auth import HTTPBasicAuth
import httplib2

def pushDynamicRule(http,input):
    #Authetication
    url = 'http://localhost:8181/restconf/operations/flowguard:add-dynamic-fwrule'

    json_data = { "input": input }
    payload = json.dumps(json_data)

    resp, content = http.request(
      uri = url,
      method = 'POST',
      headers={'Content-Type' : 'application/json'},
      body=payload
    )
    print resp


dynamic_rule1 = {
  "ruleId": "101",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "80",
  "action": "block"
}

dynamic_rule2 = {
  "ruleId": "102",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "*",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "80",
  "action": "block"
}

dynamic_rule3 = {
  "ruleId": "103",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "*",
  "sourcePort": "*",
  "destinationPort": "80",
  "action": "block"
}

dynamic_rule4 = {
  "ruleId": "104",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "*",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "block"
}

dynamic_rule5 = {
  "ruleId": "1",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "*",
  "destinationIpAddress": "*",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "allow"
}
def main():

    global dynamic_rule1
    global dynamic_rule2
    global dynamic_rule3
    global dynamic_rule4
    global dynamic_rule5


    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    
    #Push the dynamic rule
    pushDynamicRule(h,dynamic_rule1)
    pushDynamicRule(h,dynamic_rule2)
    pushDynamicRule(h,dynamic_rule3)
    pushDynamicRule(h,dynamic_rule4)
    pushDynamicRule(h,dynamic_rule5)


if __name__ == "__main__":
	main()


