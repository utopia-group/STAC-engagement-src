#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@baseline.txt -F "route_map_name=baseline" --insecure https://localhost:8443/add_route_map > /dev/null

# check the size of the response with 1 airport
hacker=`curl -s -b cookies.txt --insecure https://localhost:8443/passenger_capacity_matrix/1553932502 -w '%{size_download}\n' | tail -n1`

echo "Baseline response= $hacker"
