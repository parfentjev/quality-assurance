package ee.fakeplastictrees.test.util;

import java.util.Optional;

public class Config {
    public static final String BANK_API_URL = Optional.ofNullable(System.getenv("BANK_API_URL"))
            .orElse("http://localhost:8080");
}
