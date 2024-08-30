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

    private UserSteps user = new UserSteps();

    @Test
    @DisplayName("Изменение email и имя пользователя с авторизацией")
    public void changeUserDataTrue() {
        accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);
        result = user.getResult(accessToken, emailUserNew, nameUserNew);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUserNew));
        assertTrue(result.getUser().getName().equals(nameUserNew));
    }

    @Test
    @DisplayName("Изменение только email пользователя с авторизацией")
    public void changeUserEmailTrue() {
        accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);
        result = user.getResult(accessToken, emailUserNew, null);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUserNew));
        assertTrue(result.getUser().getName().equals(nameUser));
        emailUser = emailUserNew;
    }

    @Test
    @DisplayName("Изменение только имя пользователя с авторизацией")
    public void changeUserNameTrue() {
        accessToken = user.getAccessToken(emailUser, passwordUser, nameUser);
        result = user.getResult(accessToken, null, nameUserNew);

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getEmail().equals(emailUser.toLowerCase()));
        assertTrue(result.getUser().getName().equals(nameUserNew));
        nameUser = nameUserNew;
    }

    @Test
    @DisplayName("Попытка изменения данных пользователя без авторизации")
    public void changeUserDataWithoutLoginFalse() {
        user
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

        user.createUser(emailUser, passwordUser, nameUser);
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
