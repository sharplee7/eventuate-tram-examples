. ./scripts/_set-env.sh

export SPRING_DATASOURCE_URL="jdbc:sqlserver://${DOCKER_HOST_IP}:1433;databaseName=eventuate"
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=Eventuate123!
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.microsoft.sqlserver.jdbc.SQLServerDriver
export SPRING_PROFILES_ACTIVE=EventuatePolling
export EVENTUATELOCAL_CDC_READER_NAME=MssqlPollingReader

echo SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
