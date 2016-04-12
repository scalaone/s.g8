create database $name;format="lower"$ charset utf8 collate utf8_bin;
use $name;format="lower"$;
create table test(id bigint auto_increment primary key, name varchar(200) not null);