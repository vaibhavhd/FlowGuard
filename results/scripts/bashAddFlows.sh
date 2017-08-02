!/bin/bash

for rank in $(seq 1 1000); do
  curl -X PUT  -H "Content-Type:application/json" -H "Accept:application/json" --user admin:admin http://localhost:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/flow-node-inventory:table/0/flow/$rank --data '{"flow": [{"id": "'"$rank"'", "match": { "ethernet-match": { "ethernet-type": { "type": "2048" } },"ip-match": { "ip-protocol": "6", "ip-proto": "ipv4" }, "tcp-source-port": "'"$rank"'"},"instructions": {"instruction": [{"order": "0","apply-actions": {"action": [{"order": "0","drop-action": {}}]}}]},"flow-name": "FT10","priority": "'"$rank"'","table_id": "10"}]}';
done
