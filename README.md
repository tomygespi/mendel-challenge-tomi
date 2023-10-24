# Mendel Challenge

#### Developer: [Tomas Espinosa](https://www.linkedin.com/in/ttomasespinosa/)

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Java 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/what-is-corretto-17.html)
- [Postman](https://www.postman.com/) or similar (Optional)

### Containerize your application
Open a new terminal, go to the root directory (where the Dockerfile is located) and execute the following commands:

`docker build --tag java-docker .`

After it has finished, execute the following command to check if the docker image was created successfully:

`docker images`

Example output:

![img.png](img.png)