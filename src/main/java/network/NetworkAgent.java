package network;

import io.restassured.config.RestAssuredConfig;
import io.restassured.response.ValidatableResponse;
import io.restassured.config.RedirectConfig;
import io.restassured.config.SSLConfig;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class NetworkAgent {
    private final String CONTENT_TYPE_JSON = "application/json";

    private final RestAssuredConfig config = RestAssuredConfig.newConfig()
            .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
            .redirect(RedirectConfig.redirectConfig().followRedirects(true));

    private RequestSpecification initializeRequest() {
        return given().config(config).header("Content-Type", CONTENT_TYPE_JSON);
    }

    private RequestSpecification initializeAuthenticatedRequest(String accessToken) {
        return initializeRequest().auth().oauth2(accessToken);
    }

    protected ValidatableResponse performGetRequest(String uri) {
        return initializeRequest().get(uri).then();
    }

    protected ValidatableResponse performGetRequest(String uri, String accessToken) {
        return initializeAuthenticatedRequest(accessToken).get(uri).then();
    }

    protected ValidatableResponse executePostRequest(String uri, Object body) {
        return initializeRequest().body(body).post(uri).then();
    }

    protected ValidatableResponse executePostRequest(String uri, Object body, String accessToken) {
        return initializeAuthenticatedRequest(accessToken).body(body).post(uri).then();
    }

    protected ValidatableResponse executePatchRequest(String uri, Object body) {
        return initializeRequest().body(body).patch(uri).then();
    }

    protected ValidatableResponse executePatchRequest(String uri, Object body, String accessToken) {
        return initializeAuthenticatedRequest(accessToken).body(body).patch(uri).then();
    }

    protected ValidatableResponse executeDeleteRequest(String uri, String accessToken) {
        return initializeAuthenticatedRequest(accessToken).delete(uri).then();
    }
}
