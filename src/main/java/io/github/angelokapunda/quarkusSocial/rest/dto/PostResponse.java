package io.github.angelokapunda.quarkusSocial.rest.dto;

import io.github.angelokapunda.quarkusSocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class
PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post) {
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }
}
