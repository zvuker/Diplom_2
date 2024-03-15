import network.ApiActions;
import datastruct.entity.OrdersResponse;
import datastruct.entity.PurchaseOrders;
import datastruct.AccountDetails;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import com.github.javafaker.Faker;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestCreatePurchaseOrder {
    private static final PurchaseOrders ORDER_REQUEST_WITH_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{"61c0c5a71d1f82001bdaaa7a"});
    private static final PurchaseOrders ORDER_REQUEST_EMPTY_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{});
    private static final PurchaseOrders ORDER_REQUEST_INVALID_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{"badHash"});
    private final Faker faker = new Faker(new Locale("en"));
    private final ApiActions apiActions = new ApiActions();
    private AccountDetails user;
    private List<AccountDetails> testParams;

    @Before
    public void testSetup() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    @Test
    @DisplayName("заказ, ингредиенты, без авторизации")
    public void testOrderCreationWithoutAuthorization() {
        apiActions.createOrder(ORDER_REQUEST_WITH_INGREDIENTS).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .body("order.status", equalTo(null))
                .extract().body().as(OrdersResponse.class);
    }

    @Test
    @DisplayName("заказ, авторизация, ингредиенты")
    public void testOrderCreationWithAuthorization() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        OrdersResponse ordersResponse = apiActions.createOrder(ORDER_REQUEST_WITH_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .body("order.status", equalTo("done")).and()
                .extract().body().as(OrdersResponse.class);
    }

    @Test
    @DisplayName("заказ, неправильные ингредиенты")
    public void testOrderCreationWithInvalidIngredientHash() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        apiActions.createOrder(ORDER_REQUEST_INVALID_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("заказ, нет ингредиентов")
    public void testOrderCreationWithoutIngredients() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        apiActions.createOrder(ORDER_REQUEST_EMPTY_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).and()
                .body("success", equalTo(false));
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
