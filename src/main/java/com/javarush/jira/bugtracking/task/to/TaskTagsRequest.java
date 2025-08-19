package com.javarush.jira.bugtracking.task.to;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record TaskTagsRequest(Set<@Size(min = 2, max = 32) String> tags) {

}
