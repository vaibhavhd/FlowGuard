#!/usr/bin/env python
import json
import sys
import os
from pprint import pprint

def split_at_n_delimeter(str,delimeter,n):
    result = str.split(delimeter)
    return delimeter.join(result[:n]),delimeter.join(result[n:])

def checkKey(key, rule):
	value = ""
	if key in rule.keys():
		value = rule[key]
	return value

def process_actions(actions):
	
	split_actions = actions.split(',')
	set_src_ip = ""
	set_src_mac = ""
	set_dst_ip = ""
	set_dst_mac = ""
	output = ""

	for action in split_actions:
		if 'set-src-ip' in action:
			set_src_ip = [x.strip() for x in action.split('=')][1]
		if 'set-dst-ip' in action:
			set_dst_ip = [x.strip() for x in action.split('=')][1]
		if 'set-src-mac' in action:
			set_src_mac = [x.strip() for x in action.split('=')][1]
		if 'set-dst-mac' in action:
			set_dst_mac = [x.strip() for x in action.split('=')][1]
		if 'output' in action:
			output = [x.strip() for x in action.split('=')][1]

	return set_src_ip, set_src_mac,set_dst_ip, set_dst_mac,output

def checkActionExits(actions):
	split_actions = actions.split(',')
	for action in split_actions:
		new_action = action.split('=')
		if new_action[1] == " ":
			return False
		else:
			return True


def set_field_actions(src_ip,src_mac,dst_ip,dst_mac):

	result = None
	#case 1 if src ip and src mac exist
	if src_ip != "" and src_mac != "":
		result = {
			"ipv4-source":str(src_ip + "/32") if src_ip != "" else "",
			"ethernet-source": {
				"address":src_mac
			}
		}
	#case 2 if dst ip and dst mac exist
	elif dst_ip != "" and dst_mac != "":
		result = {
			"ipv4-destination":str(dst_ip + "/32") if dst_ip != "" else "",
			"ethernet-destination": {
				"address":dst_mac
			}
		}
	#case 3 if both of the cases above exist
	elif (src_ip != "" and src_mac != "") and (dst_ip != "" and dst_mac != ""):
		result = {
			"ipv4-source":str(src_ip + "/32") if src_ip != "" else "",
			"ethernet-source": {
				"address":src_mac
			},
			"ipv4-destination":str(dst_ip + "/32") if dst_ip != "" else "",
			"ethernet-destination": {
				"address":dst_mac
			}
		}
	#else:
	#	print "No set actions"

	return result

def set_field(src_ip,src_mac,dst_ip,dst_mac):
	result = set_field_actions(src_ip,src_mac,dst_ip,dst_mac)
	if result == None:
		return None
	else:
		result = {
			"set-field":result,
			"order":"0"
		}
		return result


def action(src_ip,src_mac,dst_ip,dst_mac, openflowID, output):


	action = []
	output_port = None
	if output != "":
		output_port = {
				"output-action": {
					"output-node-connector":str(openflowID + ":" + output)
				},
				"order":"0"
			}
	set_field_result = set_field(src_ip,src_mac,dst_ip,dst_mac)

	if set_field_result == None:
		action.append(output_port)
	else:	
		action.append(set_field_result)
		action.append(output_port)
	return action


def convert_flow_rule(rule):
	set_src_ip = ""
	set_src_mac = ""

	set_dst_ip = ""
	set_dst_mac = ""
	output = ""
	
	ether_type = int(rule['ether-type'],16)
	openflowID = "openflow:" + str(int(split_at_n_delimeter(rule['switch'],':',7)[1],10))
	in_port = checkKey('ingress-port', rule)
	src_ip = checkKey('src-ip', rule)
	dst_ip = checkKey('dst-ip', rule)
	if checkActionExits(rule['actions']):
		set_src_ip, set_src_mac, set_dst_ip, set_dst_mac, output = process_actions(rule['actions'])

	# parse switch id
	flow_rule = {
		"table-id":"0",
		"hard-timeout":"0",
		"barrier":"true",
		"idle-timeout":"0",
		"id": rule['name'],
		"flow-name":rule['name'],
		"priority":"0",
		"match": {
			"ipv4-destination": dst_ip,			### putting actual address here
			"in-port": openflowID + ":" + in_port,
			"ipv4-source":src_ip,
			"ethernet-match": {
				"ethernet-type": {
					"type": str(ether_type)
				}
			}
		},
		"instructions": { 
			"instruction": [
				{
					"write-actions": {
						"action": action(set_src_ip,set_src_mac,set_dst_ip,set_dst_mac, openflowID, output)
					},
					"order":"0"
				}
			]
		}
	}
	return flow_rule

def convert_flows(filePath):
	f = open(filePath,'rb')
	flows = json.load(f)
	f.close()

	flow_list = []
	switch_list = []
	nodes = []
	for key in flows.keys():
		openflowID = "openflow:" + str(int(split_at_n_delimeter(flows[key]['switch'],':',7)[1],10))
		switch_list.append(openflowID)
		flow = flow = convert_flow_rule(flows[key])
		flow_list.append(flow)
		
	
	switch_list = list(set(switch_list))

	for switch in switch_list:
		counter2 = 0
		flow_in_same_switch=[]
		for flow in flow_list:
			openflow_switch = split_at_n_delimeter(flow['match']['in-port'],':',2)[0]
			if switch == openflow_switch:
				flow_in_same_switch.append(flow)	
		node = {
			"dpid":switch,
			"flow":flow_in_same_switch
		}
		nodes.append(node)

	final_result = {
		"nodes":nodes
	}
	
	return final_result
	
########################## Main #################################

if len(sys.argv) != 2:
	print "[Usage] Please attach the path to the file that you want to convert as argv[1]"
	exit(1)
	
#print json.dumps(convert_flows(sys.argv[1]), indent=4, sort_keys=True)

result =  convert_flows(sys.argv[1])
new_file_name = "Converted_" + os.path.basename(sys.argv[1])
print new_file_name
f = open(new_file_name,'wb')
f.write(json.dumps(result, indent=4, sort_keys=True))
f.close()
