# $name$

This is a $name$ project generated from `wabase-template.g8`.

Run `sbt ~reStart`, then browse http://localhost:8082 to explore and try API.

## To develop
``` bash
sbt ~reStart
```
Runs the application in continuous mode: compiles on file changes and restarts the process automatically. Use the ~ prefix for watch mode.

## To test
``` bash
sbt test it/test
```
Executes all unit and integration tests in the project.

## To create JAR
``` bash
sbt assembly
```
Builds a "fat" JAR using the sbt-assembly plugin: bundles all dependencies into a single executable archive.
