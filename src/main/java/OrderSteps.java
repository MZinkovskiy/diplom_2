import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    public final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public final String POST_CREATE = "/api/orders";
    public final String GET_ORDERS = "/api/orders";

    public Response createOrder(String accessToken, List<String> hash) {
        if (accessToken != null & accessToken.length() > 0) {
            accessToken = accessToken.substring(7, accessToken.length());
        }
        // Соберем строку хештегов ингредиентов
        String ingredients = "";
        for (int i = 0; i < hash.size(); i++) {
            ingredients = ingredients + "\"" + hash.get(i) + "\"";
            if (i < hash.size() - 1) {
                ingredients = ingredients + ", ";
            }
        }
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body("{\n" +
                        "    \"ingredients\": [" + ingredients + "]" +
                        "}")
                .when()
                .post(POST_CREATE);
    }

    public ValidatableResponse getOrders(String accessToken) {
        if (accessToken != null & accessToken.length() > 0) {
            accessToken = accessToken.substring(7, accessToken.length());
        }
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .filter(new ErrorLoggingFilter())
                .get(GET_ORDERS)
                .then();
    }
}
