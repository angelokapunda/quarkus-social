package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.Follower;
import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.domain.repository.FollowerRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.UserRepository;
import io.github.angelokapunda.quarkusSocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerControllerTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowerRepository followerRepository;

    private Long userId;
    private Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {

        // Usuário padrão para os testes
        var user = new User();
        user.setName("Angelo dos Santos");
        user.setAge(26);
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setName("Santos Dicaprio");
        follower.setAge(16);
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @Order(1)
    @DisplayName("Should return 409 when follower id is equals to user id")
    public void someUserAsFollowerTes() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(409)
            .body(Matchers.is("You can´t yourself"));

    }

    @Test
    @Order(2)
    @DisplayName("Should return 404 on follw a user when user id doen't exist")
    public void userNotWhenTryngToFollowerFoundTes() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var userInexistent = 999;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userInexistent)
        .when()
            .put()
        .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    @DisplayName("Should follow a user")
    public void followUserTes() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(204);
    }

    @Test
    @Order(4)
    @DisplayName("Should return 404 on list user follower and user id doen't exist")
    public void userNotfoundWhenListingFollowersTes() {
        var userInexistent = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userInexistent)
        .when()
            .get()
        .then()
            .statusCode(404);
    }


    @Test
    @Order(5)
    @DisplayName("Should list a user's followers")
    public void listFollowersTest() {
        var response =
            given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
            .when()
                .get()
            .then()
                .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        assertEquals(200, response.statusCode());
        assertEquals(1, followersCount);
    }

    @Test
    @Order(6)
    @DisplayName("Should return 404 on unfollow user and user id doen't exist")
    public void userNotfoundWhenUnfollowingAUserTes() {
        var userInexistent = 999;

        given()
            .pathParam("userId", userInexistent)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("Should Unfollow an User")
    public void unfollowUserTest() {

        given()
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(204);
    }
}