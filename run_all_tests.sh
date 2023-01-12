#!/usr/bin/env bash
sbt clean compile coverage scalastyle scalafmtAll test it:test component:test coverageReport
