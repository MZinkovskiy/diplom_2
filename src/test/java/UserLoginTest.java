import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;

public class UserLoginTest {
    String emailUser;
    String passwordUser;
    String nameUser;
    String accessToken;

    private UserSteps user = new UserSteps();

    @Test
    @DisplayName("Успешная авторизация пользователем")
    @Step("Успешная авторизация пользователем")
    public void loginUserTrue() {
        user
                .loginUser(emailUser, passwordUser, nameUser)
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином (email) не возможна")
    @Step("Попытка авторизации с неверным email")
    public void loginUserEmailFalse() {
        String emailUser2 = randomAlphabetic(8) + "@yandex.ru";

        user
                .loginUser(emailUser2, passwordUser, nameUser)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем не возможна")
    @Step("Попытка авторизации с неверным паролем")
    public void loginUserPasswordFalse() {
        String passwordUser2 = randomAlphabetic(10);
        user
                .loginUser(emailUser, passwordUser2, nameUser)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
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
            accessToken = user.loginUser(emailUser, passwordUser, nameUser)
                    .extract()
                    .body()
                    .path("accessToken");
        }
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

}
