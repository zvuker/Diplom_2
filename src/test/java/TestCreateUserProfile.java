import datastruct.AccountDetails;
import network.ApiActions;
import datastruct.RegistrationReply;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.junit4.DisplayName;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class TestCreateUserProfile {
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private final ApiActions apiActions = new ApiActions();
    private final Faker faker = new Faker(new Locale("en"));
    private final static String MISSING_DATA_ERROR = "Email, password and name are required fields";

    @Before
    public void testSetUp() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    @Test
    @DisplayName("создать нового пользователя")
    public void testRegisterUserAndVerifyResponse() {
        apiActions.createUser(user).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .extract().body().as(RegistrationReply.class);
    }

    @Test
    @DisplayName("создать уже зарегистрированного пользователя")
    public void testCreateDuplicateAccountsNotAllowed() {
        ValidatableResponse createFirst = apiActions.createUser(user);
        int statusCode;
        statusCode = createFirst.extract().statusCode();
        assertThat("Пользователь создан. Код 200", statusCode, equalTo(HttpStatus.SC_OK));
        ValidatableResponse createSecond = apiActions.createUser(user);
        statusCode = createSecond.extract().statusCode();
        assertNotEquals("Статус код не должен быть 201", statusCode, equalTo(HttpStatus.SC_OK));
    }

    @Test
    @DisplayName("создать пользователя, незаполненное поле 'name'")
    public void testCreateUserWithoutNameFails() {
        user = new AccountDetails();
        testParams.add(user);
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        apiActions.createUser(user).assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN).and()
                .body("message", equalTo(MISSING_DATA_ERROR));
    }
    @Test
    @DisplayName("создать пользователя, незаполненное поле 'email'")
    public void testCreateUserWithoutEmailFails() {
        user = new AccountDetails();
        testParams.add(user);
        user.setPassword(faker.internet().password());
        user.setName(faker.name().firstName());
        apiActions.createUser(user).assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN).and()
                .body("message", equalTo(MISSING_DATA_ERROR));
    }

    @Test
    @DisplayName("создать пользователя, незаполненное поле 'password'")
    public void testCreateUserWithoutPasswordFails() {
        user = new AccountDetails();
        testParams.add(user);
        user.setEmail(faker.internet().emailAddress());
        user.setName(faker.name().firstName());
        apiActions.createUser(user).assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN).and()
                .body("message", equalTo(MISSING_DATA_ERROR));
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
