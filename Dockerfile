From openjdk:11
copy ./target/*.jar DemoApplication.jar
CMD ["java","-jar","DemoApplication.jar"]