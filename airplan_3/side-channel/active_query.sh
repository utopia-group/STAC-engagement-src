#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@hacker.txt -F "route_map_name=map_one" --insecure https://localhost:8443/add_route_map > /dev/null

cutoff=`curl -s -b cookies.txt -F "weight-type=Cost" --insecure https://localhost:8443/map_properties/1553932502 -w '%{size_download}\n' | tail -n1` > /dev/null

echo "Map is Not Fully Connected if user response is larger than=" $cutoff 
