package ru.yandex.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("SubTasks")
class SubTaskTest {
@DisplayName("from same Epic should be equals by ID")
    @Test
    void shouldBeEqualsById() {
    SubTask subTask1 = new SubTask("subTask1", "desc", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00"), 1);
    SubTask subTask2 = new SubTask("subTask2", "desc", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-02T10:00"), 1);

        assertEquals(subTask2,subTask1, "should be equals by ID");
    }

    @Test
    void shouldBeNotEqualsFromDifferentEpics() {
        SubTask subTask1 = new SubTask("subTask1", "desc", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00"), 1);
        SubTask subTask2 = new SubTask("subTask2", "desc", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-02T10:00"), 2);

        assertNotEquals(subTask2,subTask1, "from diff Epics should be not equals by ID");

    }
}