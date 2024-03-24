package ee.fakeplastictrees.test.transformer;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import ee.fakeplastictrees.test.model.LoanCalculatorRequest;
import ee.fakeplastictrees.test.model.LoanCalculatorResponse;
import ee.fakeplastictrees.test.util.JsonUtil;

public class LoanCalculatorRequestTransformer implements ResponseTransformerV2 {
    @Override
    public String getName() {
        return "loan-calculator-transformer";
    }

    @Override
    public Response transform(Response response, ServeEvent serveEvent) {
        var requestBody = JsonUtil.fromJson(serveEvent.getRequest().getBodyAsString(), LoanCalculatorRequest.class);
        var price = requestBody.propertyPrice();
        var term = BigDecimal.valueOf(requestBody.loanTerm());
        var responseBody = new LoanCalculatorResponse(price.divide(term, 2, RoundingMode.HALF_UP));

        return Response.Builder.like(response)
                .but()
                .body(JsonUtil.toJson(responseBody))
                .build();
    }

    // By default transformations will be applied globally. If you only want them to
    // apply in certain cases you can refer to make them non-global by adding this
    // to your transformer class.
    //
    // https://wiremock.org/docs/extensibility/transforming-responses/#non-global-transformations
    @Override
    public boolean applyGlobally() {
        return false;
    }
}
