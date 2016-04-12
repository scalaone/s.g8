# $name$ #

## Build & Run ##

```sh
\$ cd $name;format="snake"$
\$ ./sbt
> jetty:start
```

manually open [http://localhost:9090/](http://localhost:9090/) in your browser.

## Slick Hello World ##
```sh
mysql -uroot -proot < src/main/resources/init.sql
\$ ./sbt
> slick
> exit
cp -R src/main/slick src/main/scala
```

edit $name;format="Camel"$Swagger.scala
