import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.is;

public class UserCreateTest {
    String emailUser;
    String passwordUser;
    String nameUser;
    String accessToken;

    private UserSteps user = new UserSteps();

    @Test
    @DisplayName("Создание уникального пользователя")
    @Step("Создание уникального пользователя")
    public void createUserTrue() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);

        user
                .createUser(emailUser, passwordUser, nameUser)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Step("Попытка создания пользователя с существующим email")
    public void createDoubleUserFalse() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);
        String nameUser2 = randomAlphabetic(13);

        // Создадим пользователя первый раз
        user
                .createUser(emailUser, passwordUser, nameUser)
                .then()
                .statusCode(200)
                .body("success", is(true));

        // Попробуем создать дубликат
        user
                .createUser(emailUser, passwordUser, nameUser2)
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Step("Создание пользователя без email")
    public void createUserWithoutEmailFalse() {
        emailUser = "";
        passwordUser = randomAlphabetic(10);
        nameUser = randomAlphabetic(12);

        user
                .createUser(emailUser, passwordUser, nameUser)
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Step("Создание пользователя без пароля")
    public void createUserWithoutPasswordFalse() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = "";
        nameUser = randomAlphabetic(12);

        user
                .createUser(emailUser, passwordUser, nameUser)
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Step("Создание пользователя без имени")
    public void createUserWithoutNameFalse() {
        emailUser = randomAlphabetic(8) + "@mail.ru";
        passwordUser = randomAlphabetic(10);
        nameUser = "";

        user
                .createUser(emailUser, passwordUser, nameUser)
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void dataCleaning() {
        accessToken = user.loginUser(emailUser, passwordUser, nameUser)
                .extract()
                .body()
                .path("accessToken");
        if (accessToken != null) {
            user.deleteUser(accessToken);
        }
    }

}
