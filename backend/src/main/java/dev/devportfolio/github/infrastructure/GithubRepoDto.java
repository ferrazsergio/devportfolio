package dev.devportfolio.github.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepoDto(Long id, String name, @JsonProperty("full_name") String fullName, String description,
        @JsonProperty("html_url") String htmlUrl, String language, boolean fork, boolean archived) {
}
