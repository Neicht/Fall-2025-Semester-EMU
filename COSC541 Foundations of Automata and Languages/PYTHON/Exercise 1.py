#!/usr/bin/env python3
import re;

s = "aabb";


# (a^n b^m; n==m)
s1 = "aaabbb"
n = 0
for m in re.finditer(r"b", s1):
    n+=1;
print(n)
print(bool(re.fullmatch(r"a{n}b{n}", s1)));

s2 = "abc </p> <p> first </p> <p> second </p> n </p> <p> a";
print(re.findall(r"[<]p>[^/]*/p[>]", s2))

s3 = "123"
print(bool(re.fullmatch(r"\w{3,12}" , s3)));

s4 = "1234-5678-9012-3456"
print(re.sub(r"\d{4}-\d{4}-\d{4}", "XXXX-XXXX-XXXX",s4))

s5 = "aabbb"
print(bool(re.fullmatch(r"aaa*bbbb*", s5)))
