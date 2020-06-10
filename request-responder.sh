#!/bin/bash
readonly MY_PATH="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
java -cp "$MY_PATH/lib/fixml-connection-test-tool.jar" "de.deutscheboerse.fixml.RequestResponder" "$@"
