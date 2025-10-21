#!/usr/bin/env python3
import re

s = "aabb"


# (a^n b^m; n==m)
s1 = "ab"
n = re.findall(r"a", s1).__len__()
m = re.findall(r"b", s1).__len__()
print((bool(n == m) & bool(re.findall(r"ba", s1).__len__() == 0 )))

s2 = "abc </p> <p> first </p> <p> second </p> n </p> <p> a"
print(re.findall(r"<p>[^/]*/p>", s2))

s3 = "123"
print(bool(re.fullmatch(r"\w{3,12}" , s3)))

s4 = "1234-5678-9012-3456"
print(re.sub(r"\d{4}-\d{4}-\d{4}", "XXXX-XXXX-XXXX",s4))

s5 = "aabbb"
print(bool(re.fullmatch(r"aa+bbb+", s5)))
