#!/usr/bin/env python3


# Find n^2
def square(n):
	if n>0:
		return square(n - 1) + ((2 * n) - 1)
	else:
		return 0
	
# Find 2^n
def twosquare(n):
	if n>0:
		return (twosquare(n-1) + ((2^n+1)-1))
	else:
		return 0

def rt(l):
	if l.__len__() == 0:
		return 0;
	else:
		t = l.pop();
		return rt(l) + t;
	
# Find n^3
def cube(n):
	if n>0:
		return cube(n-1) + ((3*(square(n)) -n)-(2*n -1))
	else:
		return 0
	
# Even = 0 odd = 1
def evenOdd(n):
	if n>0:
		return int(not(evenOdd(n-1)))
	else:
		return 0
	
# Russian Peasant Multiplication
def rpm(a,b):
	if a>0:
		print(a, b)
		return rpm(int(a/2), b*2) + b*(a%2)
	else: 
		return 0
	
# Factorial sum of n
def factorial(n):
	if n>0:
		return factorial(n-1) * n
	else:
		return 1
	
# Fibonacci
def fib(n):
	if n>1:
		return fib(n-1) + fib(n-2)
	else:
		return 1
	
# Sum of Squares
def sumOfSquares(n):
	if n>0:
		return sumOfSquares(n-1) + (n*n)
	else:
		return 0
	
# Add only even numbers up to n
def even(n):
	if n==0:
		return 0
	else:
		return even(n-1) + (n*(n%2==0))
	
	

# Test
n = 2
a = 4
b = 5

print(fib(b))