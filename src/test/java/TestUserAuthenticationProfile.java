import datastruct.AccountDetails;
import datastruct.RegistrationReply;
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
    @DisplayName("логин, существующий пользователь")
    public void testLoginExistingUserReturnsUserProfile() {
        apiActions.createUser(user);
        apiActions.login(user).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .extract().body().as(RegistrationReply.class);
    }

    @Test
    @DisplayName("неправильный логин или пароль - ошибка")
    public void testLoginWithIncorrectCredentialsShowsError() {
        String expectMessage = "email or password are incorrect";
        apiActions.createUser(user);
        AccountDetails wrongAccount = new AccountDetails(faker.internet().emailAddress(), user.getPassword(), user.getName());
        testParams.add(wrongAccount);
        apiActions.login(wrongAccount).assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED).and()
                .body("message", equalTo(expectMessage));
        wrongAccount = new AccountDetails(user.getEmail(), faker.internet().password(), user.getName());
        testParams.add(wrongAccount);
        apiActions.login(wrongAccount).assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED).and()
                .body("message", equalTo(expectMessage));
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
