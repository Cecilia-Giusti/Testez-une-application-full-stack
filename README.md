#  Testez-une-application-full-stack

Testez-une-application-full-stack is a full-stack application testing project utilizing essential dependencies like Spring Boot, MySQL, JJWT, Lombok, Mapstruct, JUnit, Maven, AssertJ, H2, and Mockito.

## ️ Settings

### Step 1 - Prerequistes :

Make sure the following softs are installed

- Angular CLI version 14.1.0.
- Java JDK 17
- Maven
- MySQL >= 8


### Step 2 - Database 
- Start MySql
- Create the BDD by importing the SQL script located in ./resources/script.sql
- Add in your properties :
  - spring.datasource.username: (username)
  - spring.datasource.password: (password)
  - spring.datasource.url : (url of database)

By default the admin account is:

-   login:  yoga@studio.com
-   password: test!1234

### Step 3 - Spring Security

For use JWT and create token, add in your properties :
jwt.secret= (secret code)


##  Run Locally

### Instructions

1.  Fork this repo
2.  Clone the repo onto your computer
3.  Open a terminal window in the cloned project
4.  Run the following commands:

**Back :** 
1.Install dependencies :
```bash
mvn install
```
3.Start the development mode:
```bash
java -jar app.jar
```

**Front:** 
1.Install dependencies :
```bash
npm install
```
3.Start application:
```bash
ng serve
```

## Test Api

Use Postman collection to test several routes. 
```bash
├── ressources
│   └── postman
│       ├── rental.postman_collection.json
```


## Test

### Front	

**Unitary and integration test :**

Launch : 
 ` npm run test`

for following change:
` npm run test:watch `

Coverage : 
` npm run test:coverage`

**End to end test  :**

Launch :
`npm run e2e `


Coverage : 
` npm run run e2e:coverage`

Report is available here:

> front/coverage/lcov-report/index.html

### Back

Launch : 
 ` mvn clean test`


Report is available here:

> back/target/site/index.html
