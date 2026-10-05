#!/usr/bin/env python3
"""Render JMH accessor results as a compact markdown comparison."""

import json
import sys

with open(sys.argv[1], encoding="utf-8") as result_file:
    results = {row["benchmark"].rsplit(".", 1)[-1]: row["primaryMetric"]["score"]
               for row in json.load(result_file)}

pairs = (
    ("Field get", "fieldGetReflection", "fieldGetVarHandle"),
    ("Field set", "fieldSetReflection", "fieldSetVarHandle"),
    ("Property get", "propertyGetReflection", "propertyGetMethodHandle"),
    ("Property set", "propertySetReflection", "propertySetMethodHandle"),
)

print("### Accessor JMH results (ops/s)")
print()
print("| Operation | Reflection | Handle | Change |")
print("|---|---:|---:|---:|")
for label, baseline, optimized in pairs:
    old = results[baseline]
    new = results[optimized]
    change = (new / old - 1.0) * 100.0
    print(f"| {label} | {old:,.0f} | {new:,.0f} | {change:+.1f}% |")
