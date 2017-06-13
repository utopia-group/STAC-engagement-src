#!/usr/bin/python    

import math, sys

user=int(sys.argv[1])
n = (48 - math.sqrt(48*48+4*19*(user-3180)))/(-2*19)
print int(n)
