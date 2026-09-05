package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Long id;
    private String name;
    private String email;

}
