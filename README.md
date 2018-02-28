## Transcription demo application using Spring Boot / Thymeleaf with Vuejs

#### Download front-end dependencies
npm install && bower install

#### Start dev server for hot reloads of Vue.js components: 
npm run dev

#### To generate git properties file use mvn clean package -DskipTests=true
#### To build, create/push docker image: mvn clean install docker:build docker:push -Ddocker.host=unix:///var/run/docker.sock (-DskipTests=true to also skip tests)

#### mvn clean install docker:build -DskipTests=true -Ddocker.host=unix:///var/run/docker.sock -DPOSTGRES_USER=yourUsername -DPOSTGRES_PASSWORD=yourPassword -DDOCKER_USERNAME=yourUsername -DDOCKER_PASSWORD=yourPassword

#### To build, create/push docker image: mvn clean install docker:build docker:push -Ddocker.host=unix:///var/run/docker.sock (-DskipTests=true to build without running unit tests)
#### Project supported by Jim Cosby & Lance Fallon

mvn clean install docker:build -DskipTests=true -Ddocker.host=unix:///var/run/docker.sock -DPOSTGRES_USER=postgres -DPOSTGRES_PASSWORD=production_secret -DDOCKER_USERNAME=lfallo1 -DDOCKER_PASSWORD=Snoopy08
