#Spring Boot / Thymeleaf with Vuejs
## examples include the following:
#### restcontroller and regular controllers
#### thymeleaf forms / validation
#### spring command objects / validation
#### basic spring security with login/logout handlers, role specific functionality, custom security annotations
#### thymeleaf spring security integration
#### global exception handling for restservices and separate handling for regular controllers
#### vuejs integration w/hot-reload
#### vuejs lifecycle hooks
#### interpolating thymeleaf spring variables from within vue inline components
#### integrating vue forms in server rendered pages
#### csrf configuration from spring -> thymeleaf -> vuejs 

#### To generate git properties file use mvn clean package -DskipTests=true
#### To build, create/push docker image: mvn clean install docker:build docker:push -Ddocker.host=unix:///var/run/docker.sock (-DskipTests=true to also skip tests)

#### mvn clean install docker:build -DskipTests=true -Ddocker.host=unix:///var/run/docker.sock -DPOSTGRES_USER=yourUsername -DPOSTGRES_PASSWORD=yourPassword -DDOCKER_USERNAME=yourUsername -DDOCKER_PASSWORD=yourPassword