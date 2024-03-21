import datastruct.AccountDetails;
import datastruct.entity.OrdersResponse;
import datastruct.entity.PurchaseOrders;
import network.ApiActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TestOrderCreation {
    private ApiActions apiActions;
    private AccountDetails user;
    private List<AccountDetails> testParams;
    private static final PurchaseOrders PURCHASE_ORDERS_WITH_INGREDIENTS = new PurchaseOrders();
    private static final PurchaseOrders ORDER_REQUEST_EMPTY_INGREDIENTS = new PurchaseOrders();

    @Before
    public void setUp() {
        apiActions = new ApiActions();
        testParams = new ArrayList<>();
        user = new AccountDetails("test@example.com", "password123", "John");
        testParams.add(user);
    }

    @Test
    public void testCreateOrderWithValidAuthorization() {
        OrdersResponse response = apiActions.createOrderWithValidAuthorization(user);
        assertNotNull("Response should not be null", response);
        assertNotNull("Purchase order should not be null", response.getPurchaseOrder());
    }

    @Test
    public void testCreateOrderWithoutAuthorization() {
        apiActions.createOrderWithoutAuthorizationAndVerifyResponse();
    }

    @After
    public void cleanData() {
        apiActions.delete(testParams);
    }
}
