package com.javarush.jira.bugtracking.task;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class TaskTagId implements Serializable {
    private Long taskId;
    private String tag;

    public TaskTagId(Long taskId, String tag) {
        this.taskId = taskId;
        this.tag = tag;
    }
}
