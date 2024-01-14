
## How To run
### build docker image on the local

```shell
 $ docker build -t exchange-portal . --no-cache
```
### run the docker compose on the local

```shell
$ docker-compose up -d
```

### The docker compose will run the serval service
```shell
zookeeper port: 2181
kafka broker port: 9091
redis port: 6379
database(mariadb) port: 3306
exchange-portal port : 8080
```

### API Swagger Doc:
```shell
http://localhost:8080/swagger-ui.html#/
```
### API Access token with different region
- Access token tw: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhY2NvdW50SWJhblwiOlwiQ0g5My0wMDAwLTAwMDAtMDAwMC0wMDAwLTBcIixcImxvY2FsZVwiOlwidHdcIn0iLCJjcmVhdGVkIjoxNzA1MDQ0MjA0OTQ1LCJleHAiOjE3MzYxNDgyMDR9.9cHN-7-y58JGpyTbku0Sgfjeqjt6-GrbQ5eXHO1AadECh6iv3HZ_VmejJUxbVw7PAyJuRhvhnnCO1t1292WqWg
- Access token us: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhY2NvdW50SWJhblwiOlwiQ0g5My0wMDAwLTAwMDAtMDAwMC0wMDAwLTBcIixcImxvY2FsZVwiOlwidXNcIn0iLCJjcmVhdGVkIjoxNzA1MDQ0Mjg2Mzg0LCJleHAiOjE3MzYxNDgyODZ9.C70VX_9PcQ6BbW6lQD5wlQI_gfRQbJBMbhsKZbVNJj0ux65NK12n08D87njufc830lHODPV5h0bMslccvt92AQ
- Access token JP: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhY2NvdW50SWJhblwiOlwiQ0g5My0wMDAwLTAwMDAtMDAwMC0wMDAwLTBcIixcImxvY2FsZVwiOlwianBcIn0iLCJjcmVhdGVkIjoxNzA1MDQ0ODA0NTk1LCJleHAiOjE3MzYxNDg4MDR9.mP9kU0dIrcnvB0dW9RxJlc-fEDTK9yKSpUxiyzcdB8QaBfQHiopsxpnamjyz3vk86ah8Y-GH_9CEB7cPmdwS1w



