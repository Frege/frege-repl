#!/bin/bash

./gradlew uploadArchives -DsonatypeUsername="${SONATYPE_USERNAME}" -DsonatypePassword="${SONATYPE_PASSWORD}" -i -s
RETVAL=$?

if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
else
    echo 'Publish failed.'
    return 1
fi
