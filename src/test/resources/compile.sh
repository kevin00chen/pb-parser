#!/bin/bash

pwd=`pwd`
SRC_DIR="$pwd/src/test/resources"
TAR_DIR="$pwd/src/test/java"
protoc -I=$SRC_DIR --java_out=$TAR_DIR $SRC_DIR/test.proto