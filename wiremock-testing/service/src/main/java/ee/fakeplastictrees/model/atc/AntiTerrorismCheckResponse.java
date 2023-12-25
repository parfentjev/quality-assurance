package ee.fakeplastictrees.model.atc;

import java.math.BigDecimal;

public record AntiTerrorismCheckResponse(AntiTerrorismCheckResult result, BigDecimal score) {
}
