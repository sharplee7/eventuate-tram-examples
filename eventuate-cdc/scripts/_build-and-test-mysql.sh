#! /bin/bash

export TERM=dumb

set -e

. ./scripts/set-env-mysql-binlog.sh

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi

./gradlew ${GRADLE_OPTS} testClasses

docker-compose -f docker-compose-${database}.yml down -v --remove-orphans

docker-compose -f docker-compose-${database}.yml build
docker-compose -f docker-compose-${database}.yml up -d

./scripts/wait-for-mysql.sh

./gradlew $* -x :eventuate-local-java-cdc-connector-postgres-wal:test -x :eventuate-local-java-cdc-connector-postgres-wal:test -x eventuate-local-java-cdc-connector-e2e-tests:test -x eventuate-tram-cdc-connector-e2e-tests:test

docker-compose -f docker-compose-${database}.yml down -v --remove-orphans
