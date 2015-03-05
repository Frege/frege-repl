#!/bin/bash

./gradlew uploadArchives -PsonatypeUsername="${SONATYPE_USERNAME}" -PsonatypePassword="${SONATYPE_PASSWORD}" -i -s
RETVAL=$?

if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
else
    echo 'Publish failed.'
    return 1
fi
