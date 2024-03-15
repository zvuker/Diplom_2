import network.ApiActions;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import junitparams.Parameters;
import io.qameta.allure.junit4.DisplayName;
import static org.hamcrest.CoreMatchers.equalTo;
import com.github.javafaker.Faker;
import junitparams.JUnitParamsRunner;
import datastruct.RegistrationReply;
import datastruct.UserProfile;
import datastruct.AccountDetails;
import org.apache.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;


@RunWith(JUnitParamsRunner.class)
public class TestAccountModification {
    private AccountDetails user;
    private static final String AUTHORIZATION_ERROR = "You should be authorised";
    private List<AccountDetails> testParams;
    private final Faker faker = new Faker(new Locale("en"));
    private final ApiActions apiActions = new ApiActions();

    @Before
    public void setUp() {
        testParams = new ArrayList<>();
        user = new AccountDetails(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        testParams.add(user);
    }

    public UserProfile[] testParams() {
        return new UserProfile[]{
                new UserProfile().setEmail(faker.internet().emailAddress()).setName(faker.name().firstName()),
                new UserProfile().setEmail(faker.internet().emailAddress()),
                new UserProfile().setName(faker.name().firstName()),
                new UserProfile()
        };
    }

    @Test
    @Parameters(method = "testParams")
    @DisplayName("изменить данные пользователя, с авторизацией и без авторизации")
    public void modifyAnyFieldSuccessful(UserProfile userProfile) {
        apiActions.createUser(user);
        String accessToken = apiActions.login(user).extract().body().jsonPath().getString("accessToken").substring(7);
        if (userProfile.getEmail() != null) {
            user.setEmail(userProfile.getEmail());
        }
        apiActions.patchUser(userProfile).assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED).and()
                .body("success", equalTo(false)).and()
                .body("message", equalTo(AUTHORIZATION_ERROR));
        apiActions.patchUser(userProfile, accessToken).assertThat()
                .statusCode(HttpStatus.SC_OK).and()
                .body("success", equalTo(true)).and()
                .extract().body().as(RegistrationReply.class);
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
