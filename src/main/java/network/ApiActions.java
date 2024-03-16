package network;

import datastruct.AuthenticationData;
import datastruct.AccountDetails;
import datastruct.UserProfile;
import datastruct.entity.PurchaseOrders;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import java.util.List;


import static io.restassured.RestAssured.given;

public class ApiActions extends NetworkAgent {
    private final String apiBaseUrl = "https://stellarburgers.nomoreparties.site/api";

    @Step("получить ответ со списком ингредиентов")
    public ValidatableResponse getIngredientsResponse() {
        return performGetRequest(apiBaseUrl + "/ingredients");
    }

    @Step("извлечь список ингредиентов из ответа")
    public List<String> getIngredients() {
        ValidatableResponse response = getIngredientsResponse();
        return response.extract().jsonPath().getList("ingredients");
    }

    @Step("авторизация")
    public ValidatableResponse login(AccountDetails account) {
        AuthenticationData body = new AuthenticationData(account);
        return executePostRequest(apiBaseUrl + "/auth/login", body);
    }

    @Step("создать пользователя")
    public ValidatableResponse createUser(AccountDetails account) {
        return executePostRequest(apiBaseUrl + "/auth/register", account);
    }

    @Step("изменить данные пользователя")
    public ValidatableResponse patchUser(UserProfile modify, String accessToken) {
        return executePatchRequest(apiBaseUrl + "/auth/user", modify, accessToken);
    }

    @Step("изменить данные пользователя без авторизации")
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

    @Step("заказ с авторизацией")
    public ValidatableResponse createOrder(PurchaseOrders purchaseOrders, String accessToken) {
        return executePostRequest(apiBaseUrl + "/orders", purchaseOrders, accessToken);
    }

    @Step("заказ без авторизации")
    public ValidatableResponse createOrder(PurchaseOrders purchaseOrders) {
        return executePostRequest(apiBaseUrl + "/orders", purchaseOrders);
    }
    @Step("заказы конкретного пользователя с авторизацией")
    public ValidatableResponse getOrders(String accessToken) {
        return performGetRequest(apiBaseUrl + "/orders", accessToken);
    }

    @Step("заказы пользователя без авторизации")
    public ValidatableResponse getOrders() {
        return performGetRequest(apiBaseUrl + "/orders");
    }
}
