FROM mysql:8.0

# Thiết lập biến môi trường cho MySQL
ENV MYSQL_ROOT_PASSWORD=123
ENV MYSQL_DATABASE=wooden-furniture
ENV MYSQL_USER=admin
ENV MYSQL_PASSWORD=123

# Copy các file SQL khởi tạo vào container (nếu có)
#COPY ./init.sql /docker-entrypoint-initdb.d/

# Build Image Docker
# docker build -t vuongfly/wooden-furniture-mysql .
