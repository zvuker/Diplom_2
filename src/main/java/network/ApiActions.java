package network;

import datastruct.AccountDetails;
import datastruct.RegistrationReply;
import datastruct.UserProfile;
import datastruct.entity.OrdersResponse;
import datastruct.entity.PurchaseOrders;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class ApiActions extends NetworkAgent {
    private final String apiBaseUrl = "https://stellarburgers.nomoreparties.site/api";
    private static final PurchaseOrders PURCHASE_ORDERS_WITH_INGREDIENTS = new PurchaseOrders();
    private static final PurchaseOrders ORDER_REQUEST_EMPTY_INGREDIENTS = new PurchaseOrders();
    private static final PurchaseOrders ORDER_REQUEST_INVALID_INGREDIENTS = new PurchaseOrders();
    private String accessToken;

    public RegistrationReply createUser(AccountDetails user) {
        return executePostRequest("/users", user)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .extract().body().as(RegistrationReply.class);
    }

    public ValidatableResponse login(AccountDetails user) {
        return executePostRequest("/login", user);
    }

    @Step("создание заказа с авторизацией")
    public OrdersResponse createOrderWithValidAuthorization(AccountDetails user) {
        ApiActions apiActions = new ApiActions();
        createUser(user);
        String accessToken = login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        assertNotNull(accessToken, "Access token should not be null"); // Убедиться, что accessToken не пустой
        List<String> ingredients = getIngredients();
        PURCHASE_ORDERS_WITH_INGREDIENTS.setIngredients(ingredients.toArray(new String[0]));
        OrdersResponse response = apiActions.createOrder(PURCHASE_ORDERS_WITH_INGREDIENTS, accessToken)
                .assertThat()
                .body("order.status", equalTo("done")).and()
                .extract().body().as(OrdersResponse.class);
        assertNotNull(response, "Response should not be null"); // Убедиться, что ответ не пустой
        assertNotNull(response.getPurchaseOrder(), "Purchase order should not be null"); // Убедиться, что заказ не пустой
        return response;
    }

    @Step("Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationAndVerifyResponse() {
        ApiActions apiActions = new ApiActions();
        List<String> ingredients = apiActions.getIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RuntimeException("Ingredients must be provided.");
        }
        PurchaseOrders order = new PurchaseOrders();
        ValidatableResponse response = apiActions.createOrder(order);
        response.assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("Unauthorized"));
        assertNotNull(response.extract().as(PurchaseOrders.class));
    }

    @Step("создание заказа без ингредиентов")
    public void createOrderWithoutIngredients(AccountDetails user) {
        createUser(user);
        String accessToken = login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        createOrder(ORDER_REQUEST_EMPTY_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).and()
                .body("success", equalTo(false));
    }

    @Step("проверка ответа на создание пользователя")
    public void verifyUserCreationResponse(RegistrationReply registrationReply) {
        assertNotNull(registrationReply);
        assertTrue(registrationReply.isSuccess());
        assertEquals("User created successfully", registrationReply.getMessage());
    }

    @Step("проверка ответа на попытку создания дубликата пользователя")
    public void verifyDuplicateUserCreationResponse(ValidatableResponse response) {
        response.assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN) // Ожидаем статус код 403 (Forbidden)
                .body("success", equalTo(false)) // Ожидаем, что операция не успешна
                .body("message", equalTo("User already exists")); // Ожидаем сообщение о том, что пользователь уже существует
    }

    @Step("проверка ошибки отсутствия данных")
    public void verifyMissingDataError(ValidatableResponse response) {
        response.assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN) // Ожидаем статус код 403 (Запрещено), так как запрос некорректен
                .body("success", equalTo(false)) // Ожидаем, что операция не успешна
                .body("message", equalTo("Email, password and name are required fields")); // Ожидаем сообщение о том, что не хватает обязательных полей
    }

    @Step("Получение ответа с ингредиентами")
    public ValidatableResponse getIngredientsResponse() {
        return performGetRequest(apiBaseUrl + "/ingredients");
    }

    @Step("извлечение ингредиентов из ответа")
    public List<String> getIngredients() {
        ValidatableResponse response = getIngredientsResponse();
        return response.extract().jsonPath().getList("ingredients");
    }

    @Step("Изменить пользователя")
    public ValidatableResponse patchUser(UserProfile modify, String accessToken) {
        return executePatchRequest(apiBaseUrl + "/auth/user", modify, accessToken);
    }

    @Step("обновить пользователя без авторизации")
    public ValidatableResponse patchUser(UserProfile modify) {
        return executePatchRequest(apiBaseUrl + "/auth/user", modify);
    }

    @Step("удалить пользователя")
    public void delete(List<AccountDetails> accounts) {
        ValidatableResponse loginResp;
        if (!accounts.isEmpty()) {
            for (AccountDetails account : accounts) {
                loginResp = login(account);
                if (loginResp.extract().statusCode() == HttpStatus.SC_OK) {
                    executeDeleteRequest(apiBaseUrl + "/auth/user", loginResp.extract().body().jsonPath().getString("accessToken").substring(7));
                }
            }
        }
    }

    @Step("создать заказ")
    public ValidatableResponse createOrder(PurchaseOrders purchaseOrders, String accessToken) {
        return executePostRequest(apiBaseUrl + "/orders", purchaseOrders, accessToken);
    }

    @Step("создать заказ без авторизации")
    public ValidatableResponse createOrder(PurchaseOrders purchaseOrders) {
        return executePostRequest(apiBaseUrl + "/orders", purchaseOrders);
    }

    @Step("получить заказы")
    public ValidatableResponse getOrders(String accessToken) {
        return performGetRequest(apiBaseUrl + "/orders", accessToken);
    }

    @Step("получить заказы без авторизации")
    public ValidatableResponse getOrders() {
        return performGetRequest(apiBaseUrl + "/orders");
    }

    @Step("проверить ответ на все заказы")
    public void verifyAllOrdersResponse(ValidatableResponse response) {
        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Step("проверить ответ на заказы пользователя")
    public void verifyUserOrdersResponse(ValidatableResponse response) {
        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Step("получить все заказы")
    public ValidatableResponse getAllOrders(String accessToken) {
        return performGetRequest(apiBaseUrl + "/orders/all", accessToken);
    }

    @Step("создание заказа с авторизацией")
    public OrdersResponse createOrderWithAuthorizationAndVerifyResponse(AccountDetails user, String accessToken) {
        List<String> ingredients = getIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RuntimeException("Ingredients must be provided.");
        }
        PurchaseOrders order = new PurchaseOrders();
        order.setIngredients(ingredients.toArray(new String[0]));
        ValidatableResponse response = createOrder(order, accessToken);
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("order.status", equalTo("done")); // Проверяем, что статус заказа "done"
        OrdersResponse ordersResponse = response.extract().body().as(OrdersResponse.class);
        assertNotNull(ordersResponse);
        return ordersResponse;
    }

    @Step("создать заказ с недействительными ингредиентами")
    public void createOrderWithInvalidIngredients(AccountDetails user, String accessToken) {
        createUser(user);
        createOrder(ORDER_REQUEST_INVALID_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("jwt malformed")); // Уточняем ожидаемое сообщение об ошибке с токеном
    }

    @Step("создать заказ без ингредиентов и проверить ответ")
    public void createOrderWithoutIngredientsAndVerifyResponse(AccountDetails user) {
        createUser(user);
        String accessToken = login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        createOrder(ORDER_REQUEST_EMPTY_INGREDIENTS, accessToken).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST) // Ожидаем статус код 400 (Неверный запрос)
                .body("success", equalTo(false)); // Ожидаем, что операция не успешна
    }

    @Step("удалить пользователя")
    public void deleteUser(String userId) {
        executeDeleteRequest("/users/" + userId, accessToken)
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Step("Войти и вернуть сообщение об ошибке")
    public String loginAndReturnErrorMessage(AccountDetails wrongAccount) {
        return executePostRequest("/login", wrongAccount)
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract().body().jsonPath().getString("message");
    }

    @Step("Войти и вернуть профиль")
    public RegistrationReply loginAndReturnProfile(AccountDetails user) {
        return executePostRequest("/login", user)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .extract().body().as(RegistrationReply.class);
    }

}

