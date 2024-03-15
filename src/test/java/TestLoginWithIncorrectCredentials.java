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
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestLoginWithIncorrectCredentials {
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private final ApiActions apiActions = new ApiActions();
    private static final String MISSING_DATA_ERROR = "You should be authorised";
    private static final PurchaseOrders PURCHASE_ORDERS_WITH_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{"61c0c5a71d1f82001bdaaa7a",
            "61c0c5a71d1f82001bdaaa70",
            "61c0c5a71d1f82001bdaaa6e",
            "61c0c5a71d1f82001bdaaa78"});
    private final Faker faker = new Faker(new Locale("en"));

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
        OrdersResponse response = apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS, accessToken)
                .assertThat()
                .body("order.status", equalTo("done")).and()
                .extract().body().as(OrdersResponse.class);
        if (response != null && response.getPurchaseOrder() != null) {
            String orderId = response.getPurchaseOrder().get_id();
            OrdersResponse ordersResponse = apiActions.getOrders(accessToken).assertThat()
                    .statusCode(HttpStatus.SC_OK).and()
                    .body("success", equalTo(true)).and()
                    .body("orders[0]._id", equalTo(orderId))
                    .extract().body().as(OrdersResponse.class);
        } else {
            System.out.println("Order was not created successfully or response format is unexpected.");
        }
    }

    @Test
    @DisplayName("заказ без авторизации")
    public void testRetrieveOrdersWithoutAuthorization() {
        apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true));
        apiActions.getOrders().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED).and()
                .body("success", equalTo(false)).and()
                .body("message", equalTo(MISSING_DATA_ERROR));
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
