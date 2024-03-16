import org.junit.Test;
import org.junit.Before;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderAPITest {

    private String accessToken;

    @Before
    public void setUp() {
        // Выполнить запрос на авторизацию и получить токен доступа
        // Предполагается, что accessToken уже получен и сохранен
        accessToken = "Bearer your_access_token_here";
    }

    @Test
    public void testGetAllOrders() {
        given()
                .header("Authorization", accessToken)
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/orders/all")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    public void testGetUserOrders() {
        given()
                .header("Authorization", accessToken)
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}
