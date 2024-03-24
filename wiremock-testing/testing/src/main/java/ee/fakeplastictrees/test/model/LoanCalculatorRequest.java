package ee.fakeplastictrees.test.model;

import java.math.BigDecimal;

public record LoanCalculatorRequest(BigDecimal propertyPrice, Integer loanTerm) {

}
