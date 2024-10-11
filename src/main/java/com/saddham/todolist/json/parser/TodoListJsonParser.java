package com.saddham.todolist.json.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saddham.todolist.model.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TodoListJsonParser {
    ObjectMapper objectMapper;

    public TodoListJsonParser() {
        objectMapper = new ObjectMapper();
    }

    public List<Task> parse(String filename) {
        List<Task> todoList = new ArrayList<>();
        try {
            InputStream inputStream = TodoListJsonParser.class.getClassLoader().getResourceAsStream(filename);
            todoList.addAll(objectMapper.readValue(inputStream, new TypeReference<List<Task>>() {}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return todoList;
    }

    public List<String> parseAsJsonString(String filename) {
        return parse(filename).stream().map(this::toString).toList();
    }

    public String toString(Task task) {
        String taskStr;

        try {
            taskStr = objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return taskStr;
    }
}
