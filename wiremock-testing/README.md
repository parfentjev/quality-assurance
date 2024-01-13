WireMock is a tool for building mock APIs: https://wiremock.org/

Here's a blog post about this project: [https://fakeplastictrees.ee/post/9b3a4a8e-7973-4b7e-a8aa-c438a5722397](https://fakeplastictrees.ee/post/9b3a4a8e-7973-4b7e-a8aa-c438a5722397/how-test-engineers-can-use-wiremock-to-substitute-external-services)

# Usage

Run the server separately in its release configuration:

```shell
docker-compose up -d --build
```

Run the server in its test configuration along with the tests:

```shell
docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d --build
```

Don't forget to change this path `/home/aleksei/Documents/DockerVolume/` in the `docker-compose.test.yml` file to map test results to your host:

```yaml
volumes:
  - /home/aleksei/Documents/DockerVolume/:/src/testing/build/reports/
```

# Useful links

- https://wiremock.org/docs/stubbing/
- https://github.com/wiremock/wiremock/blob/master/src/test/java/ignored/Examples.java
