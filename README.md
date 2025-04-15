# Guide to run this project
*run this command before run project*  
`docker-compose up -d`

### Copy file backup data vào container 
`docker cp backup.sql wooden-furniture-mysql:/backup.sql`

### Restore database
`type backup.sql | docker exec -i wooden-furniture-mysql mysql -u root -p123 wooden-furniture`

### Backup database
`docker exec wooden-furniture-mysql mysqldump -u root -p123 wooden-furniture > backup.sql`

### Url to connect to DB through DBeaver
`jdbc:mysql://localhost:3306/wooden-furniture?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false`