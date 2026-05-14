package io.github.angelokapunda.quarkusSocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Text is requered")
    private String text;

}
