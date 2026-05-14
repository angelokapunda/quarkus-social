package io.github.angelokapunda.quarkusSocial.domain.repository;

import io.github.angelokapunda.quarkusSocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
