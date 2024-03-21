import com.github.javafaker.Faker;
import datastruct.AccountDetails;
import datastruct.UserProfile;
import io.qameta.allure.junit4.DisplayName;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
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
        if (userProfile.getEmail() != null) {
            user.setEmail(userProfile.getEmail());
        }
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
