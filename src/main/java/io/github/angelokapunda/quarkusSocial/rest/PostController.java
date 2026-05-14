package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.Post;
import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.domain.repository.FollowerRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.PostRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.UserRepository;
import io.github.angelokapunda.quarkusSocial.rest.dto.CreatePostRequest;
import io.github.angelokapunda.quarkusSocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostController {

    private PostRepository postRepository;
    private Validator validator;
    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostController(PostRepository postRepository, UserRepository userRepository, Validator validator, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.validator = validator;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest) {
        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);

        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }
        User follower = userRepository.findById(followerId);

        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Inexitent follower Id")
                    .build();
        }
        boolean follows = followerRepository.follows(follower, user);
        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();
        var postResponseList = list.stream()
                .map(post -> PostResponse.fromEntity(post))
                .toList();
        return Response.ok(postResponseList).build();
    }
}
