package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.rest.dto.CreateUserRequest;
import io.github.angelokapunda.quarkusSocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @Order(1)
    @DisplayName("Should create an user successfully")
    public void createUserTest() {
        var user = new User();
        user.setName("Angelo dos Santos");
        user.setAge(26);

        var response =
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post(apiURL)
            .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @Order(2)
    @DisplayName("Should return error when json is not valid")
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiURL)
                .then()
                    .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors =  response.jsonPath().getList("errors");
        assertNotNull(errors.getFirst().get("message"));

    }

    @Test
    @Order(3)
    @DisplayName("Should list all users")
    public void listAllUserTest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get(apiURL)
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));

    }

}