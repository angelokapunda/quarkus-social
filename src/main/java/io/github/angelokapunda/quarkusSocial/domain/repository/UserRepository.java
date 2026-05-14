package io.github.angelokapunda.quarkusSocial.domain.repository;

import io.github.angelokapunda.quarkusSocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
