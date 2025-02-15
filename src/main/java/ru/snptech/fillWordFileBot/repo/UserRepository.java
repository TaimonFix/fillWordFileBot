package ru.snptech.fillWordFileBot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.snptech.fillWordFileBot.dto.User;

public interface UserRepository extends JpaRepository<User, String> {
}
