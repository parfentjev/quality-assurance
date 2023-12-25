package ee.fakeplastictrees.test.model;

import java.math.BigDecimal;

public record TransferRequest(String recipientName, String recipientAccount, BigDecimal amount, String description) {
}
