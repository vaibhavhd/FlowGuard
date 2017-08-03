#!/usr/bin/python
import json
import requests
from requests.auth import HTTPBasicAuth
import httplib2
import argparse
import os
import sys

########################################
#-------------- Functions -------------#
########################################

def msg(name=None):                                                            
    return '''test_firewall.py
         [-a, add dynamic rules]
         [-m, modify existing rule(s)]
         [-d, delete firewall rule with the rule ID from user input]
         [-g, get all firewall rules]
         [-s, add static rule with the file path provided by user]
        '''

def pushStaticRules(filePath):
    #Authetication
    url = 'http://127.0.0.1:8181/restconf/operations/flowguard:add-static-fwrule'
    json_data = { "input": { "filePath": filePath } }
    payload = json.dumps(json_data)

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    resp, content = h.request(
      uri = url,
      method = 'POST',
      headers={'Content-Type' : 'application/json'},
      body=payload
    )
    if resp['status'] == '200' or resp['status'] == '204':
        print "[Successfully] added STATIC rules"
    else:
        print "[Failed] to add STATIC rules"

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
    
    if resp['status'] == '200' or resp['status'] == '204':
        print "[Successfully] added DYNAMIC rule",input['ruleId']
    else:
        print "[Failed] to add DYNAMIC rule",input['ruleId']


def delFwRule(input):
    #Authetication
    url = 'http://localhost:8181/restconf/config/flowguard:fwrule-registry/fwrule-registry-entry/' + str(input)

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    resp, content = h.request(
        uri = url,
        method = 'DELETE',
        headers={'Content-Type' : 'application/json'}
    )

    if resp['status'] == '200' or resp['status'] == '204':
        print "[Successfully] deleted rule",input
    else:
        print "[Failed] to delete rule",input

def delAllFwRules():
    #Authetication
    url = 'http://localhost:8181/restconf/config/flowguard:fwrule-registry'

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')

    resp, content = h.request(
        uri = url,
        method = 'DELETE',
        headers={'Content-Type' : 'application/json'}
    )
    if resp['status'] == '200' or resp['status'] == '204':
        print "[Successfully] deleted ALL firewall rules"
    else:
        print "[Failed] to delete all firewall rules"


def getFwRules():
    url='http://localhost:8181/restconf/config/flowguard:fwrule-registry'
    username = 'admin'
    password = 'admin'
    response = requests.get(url, auth=(username, password))
    
    if response.status_code == 404 or response.status_code == 400:
        print "[Failed] to retrieve rules, database is empty"
    else:
        json_data=json.loads(response.text)
        print_json(json_data)
        

def print_json(dictionary):
    rules = dictionary["fwrule-registry"]['fwrule-registry-entry']
    for rule in rules:
        print "\n"
        print "ruleid:", rule['ruleId']
        print "\tnode: ", rule['node']
        print "\tin_port: ",rule['inPort']
        print "\tsource IP address: ", rule['sourceIpAddress']
        print "\tdestination IP address: ",rule['destinationIpAddress']
        print "\tsource Port: ", rule['sourcePort']
        print "\tdestination Port: ",rule['destinationPort']
        print "\taction: ",rule['action']
    

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
  "ruleId": "1",
  "node": "openflow:2",
  "inPort": "2",
  "sourceIpAddress": "10.0.0.1/32",
  "destinationIpAddress": "10.0.0.3/32",
  "sourcePort": "*",
  "destinationPort": "5000",
  "action": "allow"
}
dynamic_rule8 = {
  "ruleId": "2",
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

print "\n"
parser = argparse.ArgumentParser(description='Test firewall policy: add | delete | modify rule(s)', usage=msg())

parser.add_argument("-a", "--add",    action = "store_true", help='add dynamic rules')
parser.add_argument("-m", "--modify", action = "store_true", help='modify existing rule(s)')
parser.add_argument("-d", "--delete", dest = "ruleId",       help = 'delete firewall rule with the rule ID from user input')
parser.add_argument("-da","--delete_all",action="store_true",help = 'delete firewall rule with the rule ID from user input')
parser.add_argument("-g", "--get",    action = "store_true", help = 'get all firewall rules')
parser.add_argument("-s", "--add-static", dest = "filePath", help = 'add static rule with the file path provided by user')

args = parser.parse_args()

if args.add == True:
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

elif args.modify == True:
    print "\nTest modifying existing rule rule #7 + #8"
    pushDynamicRule(dynamic_rule7)
    pushDynamicRule(dynamic_rule8)

elif args.ruleId != None:
    print "\nDeleting rule", args.ruleId
    delFwRule(args.ruleId)

elif args.get == True:
    print "\nGetting firewall rules"
    getFwRules()

elif args.filePath != None:
    print "\nAdding static rules"
    pushStaticRules(args.filePath)
elif args.delete_all == True:
    delAllFwRules()
else:
    parser.print_usage()

