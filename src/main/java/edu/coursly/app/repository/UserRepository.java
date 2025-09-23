package edu.coursly.app.repository;

import edu.coursly.app.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
