#!/bin/bash
echo "Testing MySQL connection"
echo "select user from mysql.user where user = 'mbt';" | mysql -u root -proot

if [ $? -eq 1 ]; then
  exit 1
fi

echo "Testing MBT user creation"
cnt=$(echo "select user from mysql.user where user = 'mbt';" | mysql -u root -proot | wc -l)

if [ $cnt -eq 0 ]; then
# TODO move this all to a single sql script
  echo "Creating MBT user"
  echo "CREATE USER 'mbt'@'%' IDENTIFIED BY 'mbt';" | mysql -u root -proot
  echo "Creating MBT database"
  echo "CREATE DATABASE mbt;" | mysql -u root -proot
  echo "Granting all privileges on MBT database to MBT user"
  echo "GRANT ALL PRIVILEGES ON mbt.* TO 'mbt'@'%';" | mysql -u root -proot
fi

exit 0