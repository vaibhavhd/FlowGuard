import json
from pprint import pprint

with open('staticflowrules.json') as data_file:
    data = json.load(data_file)

pprint(data['bbra_rtr-mid-rules-1']["actions"])
