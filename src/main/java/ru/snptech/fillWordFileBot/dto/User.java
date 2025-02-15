package ru.snptech.fillWordFileBot.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    private String id;

    @Column
    private String surname;

    @Column
    private String name;

    @Column
    private String patronymic;


}
