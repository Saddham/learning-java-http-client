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

    public List<Task> parse(File jsonFile) {
        try {
            InputStream inputStream = TodoListJsonParser.class.getClassLoader().getResourceAsStream(jsonFile.getName());
            return objectMapper.readValue(inputStream, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> parse(String todoListJson) {
        try {
            return new ArrayList<>(objectMapper.readValue(todoListJson, new TypeReference<List<Task>>() {
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Task parseSingleTask(String todoListJson) {
        try {
            return objectMapper.readValue(todoListJson, Task.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> parseAsJsonString(File file) {
        return parse(file).stream().map(this::toString).toList();
    }

    public String toString(Task task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
