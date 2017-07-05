#!/usr/bin/python
import json
import requests
from requests.auth import HTTPBasicAuth
import httplib2

########################################
#-------------- Functions -------------#
########################################
def pushDynamicRule(input):
    #Authetication
    url = 'http://localhost:8181/restconf/operations/flowguard:add-dynamic-fwrule'

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    json_data = { "input": input }
    payload = json.dumps(json_data)

    resp, content = h.request(
        uri = url,
        method = 'POST',
        headers={'Content-Type' : 'application/json'},
        body=payload
    )
    print "Status = ", resp['status']


########################################
#----------- Defining Rules -----------#
########################################

dynamic_rule1 = {
  "ruleId": "101",
  "node": "openflow:1",
  "inPort": "1",
  "sourceIpAddress": "10.0.*.*/32",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "allow"
}
dynamic_rule2 = {
  "ruleId": "102",
  "node": "openflow:1",
  "inPort": "1",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "block"
}


dynamic_rule3 = {
  "ruleId": "103",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "*",
  "destinationIpAddress": "10.0.*.*/32",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "allow"
}
dynamic_rule4 = {
  "ruleId": "104",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.3/32",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "block"
}


dynamic_rule5 = {
  "ruleId": "105",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "*",
  "action": "allow"
}
dynamic_rule6 = {
  "ruleId": "106",
  "node": "openflow:1",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.2/32",
  "sourcePort": "*",
  "destinationPort": "80",
  "action": "block"
}


dynamic_rule7 = {
  "ruleId": "101",
  "node": "openflow:2",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.3/32",
  "sourcePort": "*",
  "destinationPort": "5000",
  "action": "allow"
}
dynamic_rule8 = {
  "ruleId": "102",
  "node": "openflow:2",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.3/32",
  "sourcePort": "*",
  "destinationPort": "6000",
  "action": "block"
}

########################################
#----------- /Defining Rules ----------#
########################################



#Push the dynamic rule

print "\nTest wildcard violation rule #1 + #2: [param] source ip address"
pushDynamicRule(dynamic_rule1)
pushDynamicRule(dynamic_rule2)

print "\nTest wildcard violation rule #3 + #4: [param] destination ip address"
pushDynamicRule(dynamic_rule3)
pushDynamicRule(dynamic_rule4)

print "\nTest wildcard violation rule #5 + #6: [param] destination port"
pushDynamicRule(dynamic_rule5)
pushDynamicRule(dynamic_rule6)

print "\nTest modifying existing rule rule #7 + #8"
pushDynamicRule(dynamic_rule7)
pushDynamicRule(dynamic_rule8)

