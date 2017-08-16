#!/usr/bin/python
import json
import requests
import httplib2
import sys
import argparse 
from pathlib import Path
from requests.auth import HTTPBasicAuth

'''
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
'''
def pushStaticRules(http,filePath):

    '''
    firewall_rule_list = []
    with open(filePath) as data_file:
        data = json.load(data_file)
        entries = data["fwrule-registry-entry"]
        reqs = []
        for entry in (entries):
            rule_id = entry["ruleId"]
            body = {"input": entry}
            #print json.dumps(body, indent =4)
            url = 'http://localhost:8181/restconf/operations/flowguard:add-fwrule'
            payload = json.dumps(body)
            
            resp, content = http.request(
              uri = url,
              method = 'POST',
              headers={'Content-Type' : 'application/json'},
              body=payload
            )
    print resp
    print content
    #return reqs
    '''
    
    url = 'http://127.0.0.1:8181/restconf/config/flowguard:fwrule-registry'
    file = open(filePath,'rb')
    firewallRules = json.load(file)
    file.close()
    payload = json.dumps(firewallRules)
    #print json.dumps(firewallRules, indent=4, sort_keys=True)


    resp, content = http.request(
      uri = url,
      method = 'POST',
      headers={'Content-Type' : 'application/json'},
      body=payload
    )

    print resp
    

def msg(name=None):
    return '''flowguard.py
         [-r, add static firewall rules]
         [-f, add static flow rules]
         [-i, start Flowguard]
         [-a, Init the flow and firewall rules, and start the Flowguard]
        '''


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
    print "\n"
    parser = argparse.ArgumentParser(description='### Flowguard startup and management utility ###', usage=msg())
    parser.add_argument("-r", "--addRules",   dest = "rulesPath", help='add static firewall rules')
    parser.add_argument("-f", "--addFlows", dest = "flowsPath", help='add static flow rules')
    parser.add_argument("-i", "--startFlowguard", action = "store_true", help = 'start Flowguard')
    parser.add_argument("-a", "--all", dest = "paths", help = 'Install firewall and flow rules, and start Flowguard')

    h = httplib2.Http(".cache")
    h.add_credentials('admin', 'admin')
    
    args = parser.parse_args() 
    if args.rulesPath != None:
	if not ((Path(args.rulesPath)).is_file()):
		print "Error: File "+ args.rulesPath +" does not exist"
		sys.exit();
	#Push the list of Firewall Rules
	pushStaticRules(h, args.rulesPath)
    elif args.flowsPath != None:
	if not ((Path(flowsPath)).is_file()):
		print "Error: File "+ rulesPath +" does not exist"
		sys.exit();
	#Push the list of Flow Rules
	#pushStaticFlows(h, filePath)
    elif args.startFlowguard == True:
	#Enable Flowguard
    	startFlowguard(h)

if __name__ == "__main__":
	main()
