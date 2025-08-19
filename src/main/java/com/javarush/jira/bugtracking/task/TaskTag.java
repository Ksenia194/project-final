package com.javarush.jira.bugtracking.task;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_tag")
@IdClass(TaskTagId.class)
@Getter
@Setter
@NoArgsConstructor
public class TaskTag {

    @Id
    @Column(name = "task_id")
    private Long taskId;

    @Id
    @Column(name = "tag", length = 32)
    private String tag;

    public TaskTag(Long taskId, String tag) {
        this.taskId = taskId;
        this.tag = tag;
    }
}