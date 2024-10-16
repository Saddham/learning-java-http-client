package com.saddham;

import com.saddham.todolist.client.TodoListClient;
import com.saddham.todolist.json.parser.TodoListJsonParser;

import java.io.File;
import java.util.List;

public class App {
    public static void main(String[] args) {
        TodoListJsonParser todoListJsonParser = new TodoListJsonParser();
        List<String> todoList = todoListJsonParser.parseAsJsonString(new File("db.json"));

        TodoListClient todoListClient = new TodoListClient("<api server url>", "<api key>");
        todoListClient.createTodoList(todoList);

        String todoListJson = todoListClient.getTodoList();
        System.out.println(todoListJsonParser.parse(todoListJson));

        String taskJson = todoListClient.completeTask(1);
        System.out.println(todoListJsonParser.parseSingleTask(taskJson));

        taskJson = todoListClient.deleteTask(1);
        System.out.println(todoListJsonParser.parseSingleTask(taskJson));
    }
}
