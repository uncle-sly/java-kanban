package ru.yandex.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Epic")
class EpicTest {
    @DisplayName("should be equals by ID")
    @Test
    void shouldBeEqualById() {
        Epic epic1 = new Epic("Epic1");
        Epic epic2 = new Epic("Epic2");

        assertEquals(epic2,epic1,"should be equals by ID");

    }
}