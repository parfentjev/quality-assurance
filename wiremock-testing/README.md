WireMock is a tool for building mock APIs: https://wiremock.org/

Here's a blog post about this project: TODO.

# Usage

Run the server separately in its production configuration:

```shell
docker-compose up -d --build
```

Run the server in its test configuration along with the tests:

```shell
docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d --build
```

# Useful links

- https://wiremock.org/docs/stubbing/
- https://github.com/wiremock/wiremock/blob/master/src/test/java/ignored/Examples.java