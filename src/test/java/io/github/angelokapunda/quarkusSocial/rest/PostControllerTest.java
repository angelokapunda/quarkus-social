package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.domain.repository.UserRepository;
import io.github.angelokapunda.quarkusSocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostController.class)
class PostControllerTest {

    @Inject
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setName("Angelo dos Santos");
        user.setAge(26);
        userRepository.persist(user);
    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var userId = 1L;

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);

    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 10L;

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", inexistentUserId)
        .when()
            .post()
        .then()
            .statusCode(404);

    }

}