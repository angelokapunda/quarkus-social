package io.github.angelokapunda.quarkusSocial.rest;

import io.github.angelokapunda.quarkusSocial.domain.model.Follower;
import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.github.angelokapunda.quarkusSocial.domain.repository.FollowerRepository;
import io.github.angelokapunda.quarkusSocial.domain.repository.UserRepository;
import io.github.angelokapunda.quarkusSocial.rest.dto.FollowerPerUserResponse;
import io.github.angelokapunda.quarkusSocial.rest.dto.FollowerRequest;
import io.github.angelokapunda.quarkusSocial.rest.dto.FollowerResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerController {


    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerController(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @GET
    public Response listFollower(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var list = followerRepository.findByUser(userId);
        var responseObject = new FollowerPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream().map(FollowerResponse::new).toList();
        responseObject.setContene(followerList);
        return Response.ok(responseObject).build();
    }

    @PUT
    @Transactional
    public Response followUser (@PathParam("userId") Long userId, FollowerRequest followerRequest) {
        if (userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can´t yourself").build();
        }
        User user = userRepository.findById(userId);
        var follower = userRepository.findById(followerRequest.getFollowerId());

        if (user == null || follower == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if (!follows) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAndUser(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
