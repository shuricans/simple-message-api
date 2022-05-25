## Simple message api with JWT authorization
### Hi everyone!

### Project has two modules, short overview of the tools.

#### Database module
> common persistence module  
> contains entities, repositories, specifications
* Spring Data JPA with Hibernate
* [Postgresql](https://www.postgresql.org/) - runtime
* [H2](https://www.h2database.com/) - test
* [Liquibase maven plugin](https://docs.liquibase.com/home.html) - version control for DB
* Integration tests for custom repository methods and specifications  
  see [test]() package
#### Message-service-api module
> REST API for messages.  
> Authentication & authorization with JWT
* Spring Web
* [gson](https://github.com/google/gson)
* [java-jwt](https://github.com/auth0/java-jwt)
* [H2](https://www.h2database.com/) - test

#### Other common things:
* [Spring Security](https://spring.io/projects/spring-security)
* [Lombok](https://projectlombok.org/)
* [Docker](https://www.docker.com/)
* [Jib](https://github.com/GoogleContainerTools/jib) - Containerize java applications

### How run this project?
##### 1. First way - docker compose:

> * Images for x86 architecture, Linux.
> * [database image](https://hub.docker.com/repository/docker/shuricans/jwt-example-db) already have example data, no need mappings volumes.
> * [message-service-image](https://hub.docker.com/repository/docker/shuricans/message-service-api-app) will wait for the database service it depends on, this behavior implements with [entrypoint.sh](https://github.com/shuricans/simple-message-api/blob/master/message-service-api/src/main/jib/entrypoint.sh) & [wait-for-service.sh](https://github.com/shuricans/simple-message-api/blob/master/message-service-api/src/main/jib/wait-for-service.sh).
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* Grab [docker-compose.yml](https://github.com/shuricans/simple-message-api/blob/master/docker-compose.yml) from root directory of this repository.
* The default mapping port is 8085, you can change it in `docker-compose.yml` file.
* Open a terminal in the same directory with `docker-compose.yml`.
* Next use `docker-compose` or `docker compose` command.
* Simple run `docker compose up -d` - that's all.
* See logs via `docker compose logs -f`
* See stats via `docker stats`

##### 2. Second way - docker & IntelliJ IDEA:
> For database recommend using [database image](https://hub.docker.com/repository/docker/shuricans/jwt-example-db)  
> run it with that [docker-compose.yml](https://gist.github.com/shuricans/e3006989f8e5cd1fe9e14df527f156c3)  
> required version - JDK 11
* Clone project from github
* First of all, in parent module run `mvn clean install`
* I hope all tests will be passed =)
* Then run container with database: (use this [docker-compose.yml](https://gist.github.com/shuricans/e3006989f8e5cd1fe9e14df527f156c3))
* Then run `MessageServiceApiApplication` from `message-service-api` module.
* Default port 8080, you can change it.

### All right, how about test some requests?
* See [requests.txt](https://github.com/shuricans/simple-message-api/blob/master/requests.txt) in root directory of this repository.
* Be careful, the requests indicate port 8085, you can change it.
* Be sure you have [curl](https://curl.se/)
* Just follow tips and go!
* For example, also you can use [Postman](https://www.postman.com/)


##### Have any questions?
##### Write to me in [https://t.me/shuricans](https://t.me/shuricans)

Make love, not war!  
See ya!