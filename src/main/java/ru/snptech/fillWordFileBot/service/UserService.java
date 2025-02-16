package ru.snptech.fillWordFileBot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.snptech.fillWordFileBot.dto.User;
import ru.snptech.fillWordFileBot.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository repository;

    public User addUser(User user) {
        return repository.save(user);
    }

    public User findUserById(Long id) {
        return repository.getReferenceById(id);
    }
}
