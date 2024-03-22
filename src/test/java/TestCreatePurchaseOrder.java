import com.github.javafaker.Faker;
import datastruct.AccountDetails;
import datastruct.entity.OrdersResponse;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestCreatePurchaseOrder {
    private AccountDetails user;
    private final ApiActions apiActions = new ApiActions();
    private final Faker faker = new Faker(new Locale("en"));

    @Before
    public void setUp() {
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        apiActions.createUser(user);
    }

    @Test
    @DisplayName("создание заказа без авторизации")
    @Description("тест проверяет создание заказа без авторизации")
    public void testOrderCreationWithoutAuthorization() {
        apiActions.createOrderWithoutAuthorizationAndVerifyResponse();
    }

    @Test
    @DisplayName("создание заказа с авторизацией")
    @Description("тест проверяет создание заказа с авторизацией")
    public void testOrderCreationWithAuthorization() {
        ValidatableResponse loginResponse = apiActions.login(user);
        String accessToken = loginResponse.extract().body().jsonPath().getString("accessToken");
        OrdersResponse response = apiActions.createOrderWithAuthorizationAndVerifyResponse(user, accessToken);
        assertNotNull(response);
        assertEquals("true", response.getSuccess()); // Изменили проверку
    }

    @Test
    @DisplayName("создание заказа с неправильными ингредиентами")
    @Description("тест проверяет создание заказа с неправильными ингредиентами")
    public void testOrderCreationWithInvalidIngredientHash() {
        ValidatableResponse loginResponse = apiActions.login(user);
        String accessToken = loginResponse.extract().body().jsonPath().getString("accessToken");
        apiActions.createOrderWithInvalidIngredients(user, accessToken);
    }

    @Test
    @DisplayName("создание заказа без ингредиентов")
    @Description("тест проверяет создание заказа без ингредиентов")
    public void testOrderCreationWithoutIngredients() {
        apiActions.createOrderWithoutIngredients(user);
    }

    @After
    public void cleanData() {
        List<AccountDetails> testParams = new ArrayList<>();
        testParams.add(user);
        apiActions.delete(testParams);
    }
}
