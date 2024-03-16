import datastruct.entity.PurchaseOrders;
import datastruct.entity.OrdersResponse;
import datastruct.AccountDetails;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.junit4.DisplayName;
import network.ApiActions;
import com.github.javafaker.Faker;
import org.apache.http.HttpStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class TestCreatePurchaseOrder {
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private final ApiActions apiActions = new ApiActions();
    private final Faker faker = new Faker(new Locale("en"));
    private static final String MISSING_DATA_ERROR = "You should be authorised";
    private static final PurchaseOrders PURCHASE_ORDERS_WITH_INGREDIENTS = new PurchaseOrders();
    private static final PurchaseOrders ORDER_REQUEST_EMPTY_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{});
    private static final PurchaseOrders ORDER_REQUEST_INVALID_INGREDIENTS = new PurchaseOrders().setIngredients(new String[]{"badHash"});

    @Before
    public void setUp() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    @Test
    @DisplayName("заказ с авторизацией")
    public void testOrderCreationWithAuthorization() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        List<String> ingredients = apiActions.getIngredients();
        PURCHASE_ORDERS_WITH_INGREDIENTS.setIngredients(ingredients.toArray(new String[0]));
        OrdersResponse response = apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS, accessToken)
                .assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("order.status", equalTo("done")).and()
                .extract().body().as(OrdersResponse.class);
    }

    @Test
    @DisplayName("заказ без авторизации")
    public void testOrderCreationWithoutAuthorization() {
        List<String> ingredients = apiActions.getIngredients();
        PURCHASE_ORDERS_WITH_INGREDIENTS.setIngredients(ingredients.toArray(new String[0]));
        apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .body("order.status", equalTo(null))
                .extract().body().as(OrdersResponse.class);
    }

    @Test
    @DisplayName("заказ с неправильными ингредиентами")
    public void testOrderCreationWithInvalidIngredientHash() {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        apiActions.createOrder(ORDER_REQUEST_INVALID_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("заказ без ингредиентов")
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
