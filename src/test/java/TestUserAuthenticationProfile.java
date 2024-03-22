import com.github.javafaker.Faker;
import datastruct.AccountDetails;
import datastruct.RegistrationReply;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUserAuthenticationProfile {
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private final ApiActions apiActions = new ApiActions();
    private final Faker faker = new Faker(new Locale("en"));

    @Before
    public void testSetUp() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    @Test
    @DisplayName("cоздание заказа с авторизацией")
    @Description("тест проверяет создание заказа с валидной авторизацией и ожидает ответ с данными о заказе")
    public void testLoginExistingUserReturnsUserProfile() {
        apiActions.createUser(user);
        RegistrationReply reply = apiActions.loginAndReturnProfile(user);
        assertNotNull(reply);
    }

    @Test
    @DisplayName("неправильный логин/пароль")
    @Description("тест проверяет ошибку ввода неправильного логина/пароля")
    public void testLoginWithIncorrectCredentialsShowsError() {
        String expectMessage = "email or password are incorrect";
        apiActions.createUser(user);
        AccountDetails wrongAccount = new AccountDetails(faker.internet().emailAddress(), user.getPassword(), user.getName());
        testParams.add(wrongAccount);
        String errorMessage = apiActions.loginAndReturnErrorMessage(wrongAccount);
        assertEquals(expectMessage, errorMessage);
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
