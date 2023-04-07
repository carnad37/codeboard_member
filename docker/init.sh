docker run -d --name mariadb -v $MARIADB_MOUNT_PATH/mariadb.cnf:/etc/mysql/mariadb.cnf -v $MARIADB_MOUNT_PATH:/var/lib/mysql -p 33660:3306/tcp --env MARIADB_ROOT_PASSWORD=testdb# mariadb:latest

docker run --name member_redis -p 34001:6379 -d redis