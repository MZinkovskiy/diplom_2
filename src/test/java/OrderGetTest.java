import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class OrderGetTest {
    String emailUser;
    String passwordUser;
    String nameUser;
    String accessToken;
    String refreshToken;
    AllOrders orders;

    private UserSteps user = new UserSteps();
    private OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersWithLoginTrue() {
        accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);

        orders = orderSteps
                .getOrders(accessToken)
                .extract()
                .body()
                .as(AllOrders.class);
        assertTrue(orders.isSuccess());
    }

    @Test
    @DisplayName("Не возможно получить заказы не авторизованного пользователя")
    public void getOrdersWithoutLoginTrue() {
        orderSteps
                .getOrders("")
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Before
    public void createNewUser() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);

        user.createUser(emailUser, passwordUser, nameUser);

        accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);
        refreshToken = user.getRefreshToken(emailUser, passwordUser, nameUser);

        List<String> hash = Arrays.asList("61c0c5a71d1f82001bdaaa74", "61c0c5a71d1f82001bdaaa70");
        orderSteps.createOrder(accessToken, hash);

        user.logoutUser(refreshToken);
        accessToken = null;
        refreshToken = null;
    }

    @After
    public void dataCleaning() {
        if (accessToken == null) {
            accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);
        }
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }
}
