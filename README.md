# tokyometro-elasticsearch-client

my Elasticsearch training repository

## tokyo metro open data

* https://developer.tokyometroapp.jp/info

## Installation

Maven Build requires **Java8**.

1. Run Maven Build and Generate jar file.
2. Run java command by `java -jar target/tokyometro-elasticsearch-client-1.0-SNAPSHOT-jar-with-dependencies.jar -ck [cunsumer key] -ea [Elasticsearch ip address]`.

You can specify following options:
* `-ck [consumer key]`
  * as tokyo metro consumer key. **Required**
* `-ea [ip address]`
  * as Elasticsearch ip address. **Required**
* `-ep [port]`
  * as Elasticsearch port. **optional** default port is `9300`.
