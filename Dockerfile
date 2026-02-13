FROM eclipse-temurin:21-jre-jammy
COPY target/SB-ECommerce-Project-0.0.1-SNAPSHOT.jar SB-ECommerce-Project-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/SB-ECommerce-Project-0.0.1-SNAPSHOT.jar"]