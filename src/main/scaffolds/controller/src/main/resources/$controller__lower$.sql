create database $appName;format="lower"$ charset utf8 if not exists collate utf8_bin;
use $appName;format="lower"$;
create table $controller;format="lower"$(id bigint auto_increment primary key, name varchar(200) not null);