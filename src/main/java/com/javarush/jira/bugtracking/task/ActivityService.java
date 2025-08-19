package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.login.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.javarush.jira.bugtracking.task.TaskUtil.getLatestValue;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final TaskRepository taskRepository;

    private final Handlers.ActivityHandler handler;

    private static void checkBelong(HasAuthorId activity) {
        if (activity.getAuthorId() != AuthUser.authId()) {
            throw new DataConflictException("Activity " + activity.getId() + " doesn't belong to " + AuthUser.get());
        }
    }

    @Transactional
    public Activity create(ActivityTo activityTo) {
        checkBelong(activityTo);
        Task task = taskRepository.getExisted(activityTo.getTaskId());
        if (activityTo.getStatusCode() != null) {
            task.checkAndSetStatusCode(activityTo.getStatusCode());
        }
        if (activityTo.getTypeCode() != null) {
            task.setTypeCode(activityTo.getTypeCode());
        }
        return handler.createFromTo(activityTo);
    }

    @Transactional
    public void update(ActivityTo activityTo, long id) {
        checkBelong(handler.getRepository().getExisted(activityTo.getId()));
        handler.updateFromTo(activityTo, id);
        updateTaskIfRequired(activityTo.getTaskId(), activityTo.getStatusCode(), activityTo.getTypeCode());
    }

    @Transactional
    public void delete(long id) {
        Activity activity = handler.getRepository().getExisted(id);
        checkBelong(activity);
        handler.delete(activity.id());
        updateTaskIfRequired(activity.getTaskId(), activity.getStatusCode(), activity.getTypeCode());
    }

    private void updateTaskIfRequired(long taskId, String activityStatus, String activityType) {
        if (activityStatus != null || activityType != null) {
            Task task = taskRepository.getExisted(taskId);
            List<Activity> activities = handler.getRepository().findAllByTaskIdOrderByUpdatedDesc(task.id());
            if (activityStatus != null) {
                String latestStatus = getLatestValue(activities, Activity::getStatusCode);
                if (latestStatus == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setStatusCode(latestStatus);
            }
            if (activityType != null) {
                String latestType = getLatestValue(activities, Activity::getTypeCode);
                if (latestType == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setTypeCode(latestType);
            }
        }
    }

    @Transactional(readOnly = true)
    public Duration getTimeInProgress(long taskId) {
        List<Activity> activities = handler.getRepository().findStatusChanges(taskId);

        LocalDateTime inProgress = activities.stream()
                .filter(a -> "in_progress".equals(a.getStatusCode()))
                .map(Activity::getUpdated)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task never went to in_progress"));

        LocalDateTime readyForReview = activities.stream()
                .filter(a -> "ready_for_review".equals(a.getStatusCode()))
                .map(Activity::getUpdated)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task never went to ready_for_review"));

        return Duration.between(inProgress, readyForReview);
    }

    @Transactional(readOnly = true)
    public Duration getTimeInTesting(long taskId) {
        List<Activity> activities = handler.getRepository().findStatusChanges(taskId);

        LocalDateTime readyForReview = activities.stream()
                .filter(a -> "ready_for_review".equals(a.getStatusCode()))
                .map(Activity::getUpdated)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task never went to ready_for_review"));

        LocalDateTime done = activities.stream()
                .filter(a -> "done".equals(a.getStatusCode()))
                .map(Activity::getUpdated)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task never went to done"));

        return Duration.between(readyForReview, done);
    }
}
