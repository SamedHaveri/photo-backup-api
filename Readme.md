### Java 17 Rest API for B2B.

#### Deployed using Linux service, MySQL and Apache2 on Ubuntu Server 20.04 (on-premises datacenter).

Application is run by using fat-jar with embedded Tomcat Server running on port `8997`.

---

### Built using:

- Java 17 (Spring Boot, Spring Security, Spring Web, Spring Data JPA, JWT)
- Maven
- MySQL

### Run locally

Create database, user, and tables, set values in `application-dev.properties`, run `Application` class.  
Initial user for login/authentication can be created in `SetupDataLoader` class by setting `alreadySetup` variable
to `true` and defining login credentials.

- Currencies and receipt types are inserted on initial load by reading the list in application.properties

### Deployment

- Database: crate schema, user, and tables
- set allowedOrigins to WebConfig
- set values in application-prod.properties
- run **mvn install**
- upload generated file to **/opt/photoBackup/target**
- create/restart linux service photoBackup.service
- config iptable rules

<hr>

### IP tables config:

- don't interrupt if connection was established or related  
  `iptables -I INPUT 1 -m state --state ESTABLISHED,RELATED -j ACCEPT`
- allow internal access like localhost / 127.0.0.1  
  `iptables -I INPUT 2 -i lo -j ACCEPT`
- allow access for web  
  `iptables -I INPUT 3 -p tcp --dport 80 -j ACCEPT`
- allow access for DNS  
  `iptables -I INPUT 4 -p tcp --dport 53 -j ACCEPT`
- allow access on API port  
  `iptables -A INPUT -p tcp --dport 8997 -j ACCEPT`
- allow ssh for dev static IP  
  `iptables -I INPUT 5 -p tcp -s ***** --dport 22 -j ACCEPT`
- set DROP as default  
  `iptables -P INPUT DROP`

#### Show current rules with line numbers

`iptables -nvL --line-n`

#### Don't change rules on reboot:

`iptables-save`

#### Persist rules after reboot on ubuntu server

`apt install iptables-persistent`  
`netfilter-persistent save`