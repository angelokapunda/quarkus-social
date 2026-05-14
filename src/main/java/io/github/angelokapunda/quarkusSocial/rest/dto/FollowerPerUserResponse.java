package io.github.angelokapunda.quarkusSocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerPerUserResponse {

    private Integer followersCount;
    private List<FollowerResponse> contene;

}
