package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.Follower;
import io.github.angelokapunda.quarkusSocial.domain.model.Post;
import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.domain.repository.FollowerRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.PostRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.UserRepository;
import io.github.angelokapunda.quarkusSocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostController.class)
class PostControllerTest {

    private static final Long USERID= 1L;
    private Long userNotFollowerId;
    private Long userFollowerId;

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowerRepository followerRepository;

    @Inject
    private PostRepository postRepository;

    @BeforeEach
    @Transactional
    public void setUp() {

        // Usuário padrão para os testes
        var user = new User();
        user.setName("Angelo dos Santos");
        user.setAge(26);
        userRepository.persist(user);

        //Criação de portagem para o usuário
        Post post = new Post();
        post.setText("Hello Word!");
        post.setUser(user);
        postRepository.persist(post);

        // Usuário que não segue ninguém
        var userNotFollower = new User();
        userNotFollower.setName("Carlos Pitra");
        userNotFollower.setAge(30);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // Usuário Seguidor
        var userFollower = new User();
        userFollower.setName("Antero Pedro");
        userFollower.setAge(22);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", USERID)
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

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest() {
        var inexiatentUserId = 999;

        given()
            .pathParam("userId", inexiatentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when headerr is not present")
    public void listPosFollowerHeaderNotSendTest() {

        given()
            .pathParam("userId", USERID)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist")
    public void listPosFollowerNotFoundTest() {

        var inexistentFollowerId = 999;

        given()
            .pathParam("userId", USERID)
            .header("followerId", inexistentFollowerId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("Inexitent follower Id"));
    }

    @Test
    @DisplayName("Should return 403 when follower isn't a follower")
    public void listPosNotFollowerTest() {

        given()
            .pathParam("userId", USERID)
            .header("followerId", userNotFollowerId)
        .when()
            .get()
        .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostsTest() {

        given()
            .pathParam("userId", USERID)
            .header("followerId", userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}