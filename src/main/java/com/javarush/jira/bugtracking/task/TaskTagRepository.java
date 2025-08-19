package com.javarush.jira.bugtracking.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface TaskTagRepository extends JpaRepository<TaskTag, TaskTagId> {

    @Query(value = "SELECT tag FROM task_tag WHERE task_id = :taskId", nativeQuery = true)
    Set<String> getTags(long taskId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM task_tag WHERE task_id = :taskId", nativeQuery = true)
    void deleteAllByTaskId(long taskId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO task_tag (task_id, tag)
        VALUES (:taskId, :tag)
        """, nativeQuery = true)
    void insertTag(long taskId, String tag);
}
