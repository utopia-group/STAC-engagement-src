#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
    echo "Usage ./passive-query.sh <filename>"
    exit 1
fi

./login.sh usr pwd
filename=$1
cutoff=3585 # computed previously

# upload a route map
curl -s -L -b cookies.txt -F file=@$filename -F "route_map_name=map_one" --insecure https://localhost:8443/add_route_map > /dev/null

value=`curl -s -b cookies.txt -F "weight-type=Cost" --insecure https://localhost:8443/map_properties/1553932502 -w '%{size_download}\n' | tail -n1` > /dev/null

echo "Map is Not Fully Connected if user response is larger than=" $cutoff 
echo "User response= "$value

if [ $value -le $cutoff ]; then
	echo "Fully Connected"
else
	echo "Not Fully Connected"
fi
