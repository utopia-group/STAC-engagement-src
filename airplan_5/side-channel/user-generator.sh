#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
    echo "Usage ./user-generator.sh <number-airports>"
    exit 1
fi

# generate user input with n airports
n=$1
echo $n
for (( f = 1; f <= $n; f++ )) ; do 
	if [ $f -lt 10 ] ; 
		then echo "00$f" ; 
	elif [ $f -lt 100 ] ; 
		then echo "0$f" ;
	else 
		echo $f
	fi
done

# random number of flights between 1 and n
flights=$(( ( RANDOM % $n )  + 1 ))

echo $flights
for (( f = 1; f <= $flights; f++ )) ; do
	if [ $f -lt 10 ] ; 
		then echo "00$f 001 1 1 1 1 1 1" ; 
	elif [ $f -lt 100 ] ; 
		then echo "0$f 001 1 1 1 1 1 1" ;
	else 
		echo "$f 001 1 1 1 1 1 1"
	fi
done
