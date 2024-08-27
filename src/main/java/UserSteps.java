import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserSteps {
    public final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public final String POST_CREATE;
    public final String POST_LOGIN = "/api/auth/login";
    public final String POST_LOGOUT = "/api/auth/logout";
    public final String ALL_CHANGE = "/api/auth/user";

    public UserSteps() {
        POST_CREATE = "/api/auth/register";
    }

    public Response createUser(String email, String password, String name) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .when()
                .post(POST_CREATE);
    }

    public ValidatableResponse loginUser(String email, String password, String name) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .when()
                .post(POST_LOGIN)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body("{\n" +
                        "    \"accessToken\": \"" + accessToken + "\",\n" +
                        "}")
                .when()
                .delete(ALL_CHANGE)
                .then();
    }

    public ValidatableResponse changeUser(String accessToken, String email, String name) {
        if (accessToken != null & accessToken.length() > 0) {
            accessToken = accessToken.substring(7, accessToken.length());
        }
        String request = "{\n";
        if (email != null) {
            if (name != null) {
                request = request + "      \"email\": \"" + email + "\",\n";
                request = request + "      \"name\": \"" + name + "\"\n";
            } else {
                request = request + "      \"email\": \"" + email + "\"\n";
            }
        } else if (name != null) {
            request = request + "      \"name\": \"" + name + "\"\n";
        }
        request = request + "}";
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .filter(new ErrorLoggingFilter())
                .body(request)
                .when()
                .patch(ALL_CHANGE)
                .then();
    }

    public ValidatableResponse logoutUser(String refreshToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body("{\n" +
                        "    \"token\": \"" + refreshToken + "\",\n" +
                        "}")
                .when()
                .post(POST_LOGOUT)
                .then();
    }

}
