#!/bin/sh

# Abort on any error (including if wait-for fails).
set -e

args=""

# Waiting db if exist.
if [ -n "$DB_HOST" ] && [ -n "$DB_PORT" ]; then
  /wait-for-service.sh "$DB_HOST" "$DB_PORT"
  args="$args --spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/jwt_example"
fi

exec java -cp \
  $(cat /app/jib-classpath-file) \
  $(cat /app/jib-main-class-file) \
  $args