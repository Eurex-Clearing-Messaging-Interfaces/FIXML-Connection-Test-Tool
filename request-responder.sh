#!/bin/bash
myPath="$(dirname $(readlink -f ${BASH_SOURCE[0]}))"
java -cp ${myPath}/lib/fixml-connection-test-tool.jar de.deutscheboerse.fixml.RequestResponder $*
