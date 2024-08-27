import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class UserChangeTest {
    String emailUser;
    String passwordUser;
    String nameUser;
    String accessToken;
    ResultResponse result;

    String emailUserNew = (randomAlphabetic(10) + "@yandex.ru").toLowerCase();
    String nameUserNew = randomAlphabetic(12);

    private UserSteps userSteps = new UserSteps();

    @Step("Получение токена")
    public void getAccesToken() {
        accessToken = userSteps.loginUser(emailUser, passwordUser, nameUser)
                .extract()
                .body()
                .path("accessToken");
    }

    @Step("Получение ответа на запрос")
    public void getResult(String emailUserNew, String nameUserNew) {
        result = userSteps.changeUser(accessToken, emailUserNew, nameUserNew)
                .extract()
                .body().as(ResultResponse.class);
    }

    @Test
    @DisplayName("Изменение email и имя пользователя с авторизацией")
    public void changeUserDataTrue() {
        getAccesToken();
        getResult(emailUserNew, nameUserNew);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUserNew));
        assertTrue(result.getUser().getName().equals(nameUserNew));
    }

    @Test
    @DisplayName("Изменение только email пользователя с авторизацией")
    public void changeUserEmailTrue() {
        getAccesToken();
        getResult(emailUserNew, null);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUserNew));
        assertTrue(result.getUser().getName().equals(nameUser));
        emailUser = emailUserNew;
    }

    @Test
    @DisplayName("Изменение только имя пользователя с авторизацией")
    public void changeUserNameTrue() {
        getAccesToken();
        getResult(null, nameUserNew);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUser.toLowerCase()));
        assertTrue(result.getUser().getName().equals(nameUserNew));
        nameUser = nameUserNew;
    }

    @Test
    @DisplayName("Попытка изменения данных пользователя без авторизации")
    public void changeUserDataWithoutLoginFalse() {
        userSteps
                .changeUser("", emailUserNew, nameUserNew)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Before
    public void createNewUser() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);

        userSteps.createUser(emailUser, passwordUser, nameUser);
    }

    @After
    public void dataCleaning() {
        if (accessToken == null) {
            getAccesToken();
        }
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
