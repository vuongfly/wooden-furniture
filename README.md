# Guide to run this project
*run this command before run project*  
`docker-compose up -d`
### Copy file backup data vào container 
`docker cp backup.sql wooden-furniture-mysql:/backup.sql`
### Restore database
`docker exec -i wooden-furniture-mysql mysql -u root -p123 wooden-furniture < backup.sql`
### Backup database
`docker exec wooden-furniture-mysql mysqldump -u root -p123 wooden-furniture > backup.sql`
