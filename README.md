# Hotel Reservation System

## 1. Project Description:
This is a simple backend application designed for managing hotel reservations. The application provides features to:

- Create a new reservation
- Read all the existing reservations
- Modify specific details of an existing reservation using its unique identifier

## 2. Technologies and Tools Used:
The project is built with:

- Java 17
- Gradle
- jUnit 5
- Mockito
- Spring Boot

Please note that all the specific Spring Boot modules used are listed in the `build.gradle` file.

## 3. Prerequisites:
To run this project, you need the following software installed on your machine:

- Java JDK 17
- Gradle (you can also use the Gradle Wrapper included in the project)

## 4. Installation:
Follow these steps to get the project up and running on your local machine:

1. Extract the provided ZIP file containing the project to a directory of your choice on your local machine.
2. Open a terminal (Command Prompt on Windows or Terminal on macOS/Linux), and navigate to the directory where you extracted the project files.
3. Run `gradlew clean build` (on Windows) or `./gradlew clean build` (on macOS/Linux) to build the project. This command includes running the unit tests.
4. After a successful build, you will find a `.war` file in the `build/libs` directory. You can run the application with this command: `java -jar build/libs/hotel-reservation-system-0.0.1-SNAPSHOT.war` (please replace `hotel-reservation-system-0.0.1-SNAPSHOT.war` with the actual name of the generated war file).

Alternatively, you can run the project directly from IntelliJ IDEA or Eclipse:

- **IntelliJ IDEA:** Open the project in IntelliJ IDEA. Right-click on the `Application.java` file in the project explorer and click 'Run Application.main()'. IntelliJ IDEA will automatically take care of the build process and start the application.
- **Eclipse:** Import the project into Eclipse as an existing Gradle project. After the project is imported, right-click on the `Application.java` file and click 'Run As -> Java Application'.

The application should now be running and listening for HTTP requests on port 8080.

In case you want to deploy this WAR file to an external application server (like standalone Tomcat), you will need to copy the WAR file to the application server's webapps directory and start the server according to its instructions.

## 5. Testing:
Unit tests have been written to cover the major functionalities of the application. You can run the tests by executing `gradlew test`.

All the application's REST endpoints, along with their request/response structures and examples, can be found in the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

In addition to this, a Postman collection has been exported and added to the resources folder for your convenience. You can import this collection into your Postman client to explore and test the application's endpoints.
