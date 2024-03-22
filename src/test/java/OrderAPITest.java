import datastruct.AccountDetails;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OrderAPITest {

    private ApiActions apiActions;
    private String accessToken;

    @Before
    public void setUp() {
        apiActions = new ApiActions();
        AccountDetails user = new AccountDetails("2054@gmail.com", "1234567", "alex");
        apiActions.createUser(user);
        ValidatableResponse response = apiActions.login(user);
        accessToken = response.extract().jsonPath().getString("accessToken");
    }
    @Test
    @DisplayName("тест получения заказов пользователя")
    @Description("тест проверяет функциональность получения заказов пользователя")
    public void testGetUserOrders() {
        ValidatableResponse response = apiActions.getOrders(accessToken);
        apiActions.verifyUserOrdersResponse(response);
    }

    @Test
    @DisplayName("тест получения всех заказов")
    @Description("тест проверяет функциональность получения всех заказов")
    public void testGetAllOrders() {
        ValidatableResponse response = apiActions.getAllOrders(accessToken);
        apiActions.verifyAllOrdersResponse(response);
    }
    @After
    public void cleanUp() {
        List<AccountDetails> testParams = new ArrayList<>();
        AccountDetails user;
        testParams.add(user);
        apiActions.delete(testParams);
    }
}
