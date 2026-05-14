package io.github.angelokapunda.quarkusSocial.domain.repository;

import io.github.angelokapunda.quarkusSocial.domain.model.Follower;
import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        var paramns = Parameters.with("follower", follower).and("user", user).map();

        var query = find("follower = :follower and user = :user", paramns);
        var result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters.with("userId", userId)
                .and("followerId", followerId)
                .map();
        delete("follower.id =:followerId and user.id =:userId", params);
    }

}
