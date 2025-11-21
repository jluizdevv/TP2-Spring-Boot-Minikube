FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
    apt-get install -y mariadb-server && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /run/mysqld && \
    chown -R mysql:mysql /run/mysqld /var/lib/mysql

COPY my.cnf /etc/mysql/my.cnf

RUN service mariadb start && \
    mariadb -e "CREATE DATABASE IF NOT EXISTS loja;" && \
    mariadb -e "CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'senha123';" && \
    mariadb -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';" && \
    mariadb -e "FLUSH PRIVILEGES;"

EXPOSE 3306

CMD ["mysqld_safe"]