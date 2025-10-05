#!/usr/bin/env python3


# Find n^2
def square(n):
    """
    Calculate the square of a positive integer using a recursive method.

    This function computes the square of a given non-negative integer `n`
    recursively by summing odd numbers. It leverages the mathematical property
    that the sum of the first `n` odd numbers equals `n^2`.

    :param n: The non-negative integer whose square is to be calculated.
    :type n: int
    :return: The square of the input integer.
    :rtype: int
    """
    if n > 0:
        return square(n - 1) + ((2 * n) - 1)
    else:
        return 0


# Find 2^n
def twosquare(n):
    """
    Computes a specific sequence based on the input value n. The sequence is
    calculated recursively where n is decremented until it reaches 0. At each
    step, the recursive result accumulates an expression involving bitwise XOR
    operation. Returns the computed result for the sequence when given n.

    :param n: The non-negative integer value on which the sequence calculation
              is based
    :type n: int
    :return: The computed value of the sequence for the given input n
    :rtype: int
    """
    if n > 0:
        return (twosquare(n - 1) + ((2 ^ n + 1) - 1))
    else:
        return 0


def rt(l):
    """
    Recursively calculates the sum of all elements in a list.

    Given a list of numeric values, this function computes their sum by
    removing each item from the list and adding it recursively.

    :param l: A list of numeric elements to calculate the sum of.
    :type l: list
    :return: The sum of all elements in the list. If the list is empty,
        returns 0.
    :rtype: int or float
    """
    if l.__len__() == 0:
        return 0;
    else:
        t = l.pop();
        return rt(l) + t;


# Find n^3
def cube(n):
    """

    """
    if n > 0:
        return cube(n - 1) + ((3 * (square(n)) - n) - (2 * n - 1))
    else:
        return 0


# Even = 0 odd = 1
def evenOdd(n):
    """
    Determines if a number is even or odd through a recursive approach.

    This function uses recursion to evaluate whether a given integer `n`
    is even or odd. It works by reducing the number step-by-step and may
    involve deeper recursion with larger values of `n`.

    :param n: An integer input to determine its parity.
    :return: An integer where `0` represents even and `1` represents odd.

    """
    if n > 0:
        return int(not (evenOdd(n - 1)))
    else:
        return 0


# Russian Peasant Multiplication
def rpm(a, b):
    """
    Performs a recursive multiplication of two integers using the Russian Peasant
    Multiplication algorithm.

    This algorithm multiplies two integers by halving one integer and doubling the
    other recursively, accumulating the result based on the parity of the halved
    value.

    :param a: The first integer to multiply.
    :type a: int
    :param b: The second integer to multiply.
    :type b: int
    :return: The product of the two integers as computed by the algorithm.
    :rtype: int
    """
    if a > 0:
        print(a, b)
        return rpm(int(a / 2), b * 2) + b * (a % 2)
    else:
        return 0


# Factorial sum of n
def factorial(n):
    """
    Calculate the factorial of a given non-negative integer using recursion.

    The function computes the product of all positive integers less than or
    equal to a given number `n`. If `n` is 0, the factorial is 1 by definition.

    :param n: Integer value for which factorial is to be calculated. Must be a
        non-negative integer.
    :type n: int
    :return: The factorial of the given number.
    :rtype: int
    """
    if n > 0:
        return factorial(n - 1) * n
    else:
        return 1


# Fibonacci
def fib(n):
    """
    Calculate the nth Fibonacci number using recursion.

    This function computes the Fibonacci number at position `n` using a
    recursive approach. The Fibonacci sequence is defined such that each
    number is the sum of the two preceding ones, starting from 0 and 1.
    This implementation assumes the input `n` is a non-negative integer.

    :param n: The position in the Fibonacci sequence to calculate. Must be
              a non-negative integer.
    :type n: int
    :return: The Fibonacci number at the specified position `n`.
    :rtype: int
    """
    if n > 1:
        return fib(n - 1) + fib(n - 2)
    else:
        return 1


# Sum of Squares
def sumOfSquares(n):
    """

    """
    if n > 0:
        return sumOfSquares(n - 1) + (n * n)
    else:
        return 0


# Add only even numbers up to n
def even(n):
    """
    Recursively computes the sum of all even integers from 0 to a given number `n`.

    This function checks each integer from `n` down to 0, adding the value to the
    sum if it is even, and skips it otherwise.

    :param n: The upper boundary (inclusive) for the summation. Must be a non-negative integer.
    :type n: int
    :return: The sum of all even integers between 0 and `n`.
    :rtype: int
    """
    if n == 0:
        return 0
    else:
        return even(n - 1) + (n * (n % 2 == 0))


# Test
n = 2
a = 4
b = 5

print(fib(b))
