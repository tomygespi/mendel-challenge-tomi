# Mendel Challenge

#### Developer: [Tomas Espinosa](https://www.linkedin.com/in/ttomasespinosa/)

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Java 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/what-is-corretto-17.html)
- [Maven 3.9.5](https://maven.apache.org/install.html)
- [Postman](https://www.postman.com/) or similar (Optional)

### Running your application
Open a new terminal, go to the root directory (where the Dockerfile is located) and execute the following commands:

`docker build --tag java-docker .`

After it has finished, execute the following command to check if the docker image was created successfully:

`docker images`

Example output:

![img.png](documentation/images/docker_output.png)

Start your application by running:

`docker run -d -p 8080:8080 java-docker`

Now you can send requests with Postman to your containerized application:

![img_1.png](documentation/images/postman_example.png)

### Running your tests
Open a new terminal, go to the root directory (where the Maven files are located) and execute the following command:

`mvn test`

Example output:

![img.png](documentation/images/mvn_test_output.png)

### Api documentation

You can check our Swagger and Api docs on the following links:

- [Swagger](http://localhost:8080/swagger-ui/index.html#/)

- [Api docs](http://localhost:8080/v3/api-docs)