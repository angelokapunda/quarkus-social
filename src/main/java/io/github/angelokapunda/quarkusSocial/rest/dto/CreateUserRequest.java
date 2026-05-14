package io.github.angelokapunda.quarkusSocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name is requered")
    private String name;

    @NotNull(message = "Age is requered")
    private Integer age;

}
