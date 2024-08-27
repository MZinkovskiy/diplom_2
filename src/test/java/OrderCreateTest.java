import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.is;

public class OrderCreateTest {
    String emailUser;
    String passwordUser;
    String nameUser;
    String accessToken;

    private UserSteps user = new UserSteps();
    private OrderSteps order = new OrderSteps();

    @Step("Получение токена")
    public void getAccesToken() {
        accessToken = user.loginUser(emailUser, passwordUser, nameUser)
                .extract()
                .body()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией пользователем и с ингредиентами")
    public void createOrderTrue() {
        getAccesToken();

        List<String> hash = Arrays.asList("61c0c5a71d1f82001bdaaa74", "61c0c5a71d1f82001bdaaa70");
        order
                .createOrder(accessToken, hash)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации пользователем")
    public void createOrderWithoutLoginTrue() {
        List<String> hash = Arrays.asList("61c0c5a71d1f82001bdaaa74", "61c0c5a71d1f82001bdaaa77");
        order
                .createOrder("", hash)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Попытка создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsFalse() {
        getAccesToken();

        List<String> hash = new ArrayList<>();
        order
                .createOrder(accessToken, hash)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Попытка создания заказа с неверным хештегом ингредиентов")
    public void createOrderIngredientsErrorFalse() {
        getAccesToken();

        List<String> hash = Arrays.asList("61c0c5a71d1f82001bdaaa74", "61c0c5a70000001bdaaa77");
        order
                .createOrder(accessToken, hash)
                .then()
                .statusCode(500);
    }

    @Before
    public void createNewUser() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);

        user.createUser(emailUser, passwordUser, nameUser);
    }

    @After
    public void dataCleaning() {
        if (accessToken == null) {
            getAccesToken();
        }
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }
}
