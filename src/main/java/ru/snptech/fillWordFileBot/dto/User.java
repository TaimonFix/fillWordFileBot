package ru.snptech.fillWordFileBot.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String id;

    private String surname;

    private String name;

    private String patronymic;

    private LocalDate birthDate;

    private String Sex;

    private String photoPath;
}
