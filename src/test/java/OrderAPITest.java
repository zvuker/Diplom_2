import datastruct.AccountDetails;
import network.ApiActions;
import org.junit.Test;
import org.junit.Before;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderAPITest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    private static final String ORDERS_ENDPOINT = BASE_URL + "/orders";
    private static final String ALL_ORDERS_ENDPOINT = BASE_URL + "/orders/all";

    private String accessToken;

    @Before
    public void setUp() {
        ApiActions apiActions = new ApiActions();
        AccountDetails user = new AccountDetails("2054@gmail.com", "1234567", "alex");
        Response response = apiActions.login(user).extract().response();
        accessToken = response.then().extract().jsonPath().getString("accessToken");
    }

    @Test
    public void testGetUserOrders() {
        given()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    public void testGetAllOrders() {
        given()
                .header("Authorization", accessToken)
                .when()
                .get(ALL_ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}
