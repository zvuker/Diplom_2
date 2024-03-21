import datastruct.AccountDetails;
import datastruct.RegistrationReply;
import io.restassured.response.ValidatableResponse;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCreateUserProfile {
    private ApiActions apiActions;
    private AccountDetails user;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        apiActions = new ApiActions();
        user = new AccountDetails("test@example.com", "password123", "John");
        apiActions.createUser(user);
    }
    @Test
    public void testRegisterUserAndVerifyResponse() {
        RegistrationReply registrationReply = apiActions.createUser(user);
        String accessToken = registrationReply.getAccessToken();
        assertNotNull(registrationReply);
        assertNotNull(accessToken);
        apiActions.verifyUserCreationResponse(registrationReply);
    }
    @Test
    public void testCreateDuplicateAccountsNotAllowed() {
        response = apiActions.createUser(user);
        apiActions.verifyDuplicateUserCreationResponse(response);
    }
    @Test
    public void testCreateUserWithoutNameFails() {
        user.setName(null);
        response = apiActions.createUser(user);
        apiActions.verifyMissingDataError(response);
    }
    @Test
    public void testCreateUserWithoutEmailFails() {
        user.setEmail(null);
        response = apiActions.createUser(user);
        apiActions.verifyMissingDataError(response);
    }
    @Test
    public void testCreateUserWithoutPasswordFails() {
        user.setPassword(null);
        response = apiActions.createUser(user);
        apiActions.verifyMissingDataError(response);
    }
    @After
    public void cleanData() {
        List<AccountDetails> testParams = new ArrayList<>();
        testParams.add(user);
        apiActions.delete(testParams);
    }
}
