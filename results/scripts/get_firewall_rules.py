#!/usr/bin/python
import json
import requests
from requests.auth import HTTPBasicAuth
import httplib2


def GET_Request():
	url='http://localhost:8181/restconf/config/flowguard:fwrule-registry'
	username = 'admin'
	password = 'admin'
	response = requests.get(url, auth=(username, password))
	
	if response.status_code == 404 or response.status_code == 400:
		print "Nothing to return"
	else:
		json_data=json.loads(response.text)
		print_json(json_data)
		#return json_data

def print_json(dictionary):
	rules = dictionary["fwrule-registry"]['fwrule-registry-entry']
	print "\n"
	for rule in rules:
		print "ruleid:", rule['ruleId']
		print "\tnode: ", rule['node']
		print "\tin_port: ",rule['inPort']
		print "\tsource IP address: ", rule['sourceIpAddress']
		print "\tdestination IP address: ",rule['destinationIpAddress']
		print "\tsource Port: ", rule['sourcePort']
		print "\tdestination Port: ",rule['destinationPort']
		print "\taction: ",rule['action']


	print "\n"
	

def main():	
	GET_Request()
	


if __name__ == "__main__":
	main()