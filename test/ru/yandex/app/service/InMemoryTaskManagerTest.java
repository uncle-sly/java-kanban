package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {

        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

}