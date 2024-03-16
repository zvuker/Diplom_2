import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.junit4.DisplayName;
import network.ApiActions;
import datastruct.entity.PurchaseOrders;
import datastruct.entity.OrdersResponse;
import datastruct.AccountDetails;
import com.github.javafaker.Faker;
import org.apache.http.HttpStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class TestOrderCreation {
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private final ApiActions apiActions = new ApiActions();
    private final Faker faker = new Faker(new Locale("en"));
    private static final String MISSING_DATA_ERROR = "You should be authorised";
    private static final PurchaseOrders PURCHASE_ORDERS_WITH_INGREDIENTS = new PurchaseOrders();

    @Before
    public void setUp() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    @Test
    @DisplayName("заказ с авторизацией")
    public void testCreateOrderWithValidAuthorization() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        assertNotNull("Access token should not be null", accessToken);
        List<String> ingredients = apiActions.getIngredients();
        PURCHASE_ORDERS_WITH_INGREDIENTS.setIngredients(ingredients.toArray(new String[0]));
        OrdersResponse response = apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS, accessToken)
                .assertThat()
                .body("order.status", equalTo("done")).and()
                .extract().body().as(OrdersResponse.class);
        assertNotNull("Response should not be null", response);
        assertNotNull("Purchase order should not be null", response.getPurchaseOrder());
        String orderId = response.getPurchaseOrder().get_id();
        OrdersResponse ordersResponse = apiActions.getOrders(accessToken).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .body("orders[0]._id", equalTo(orderId))
                .extract().body().as(OrdersResponse.class);
        assertNotNull("Orders response should not be null", ordersResponse);
    }

    @Test
    @DisplayName("заказ без авторизации")
    public void testRetrieveOrdersWithoutAuthorization() {
        apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true));
        apiActions.getOrders().assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).and()  // Изменяем ожидаемый статус код
                .body("success", equalTo(false)).and()
                .body("message", equalTo(MISSING_DATA_ERROR));
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
