package com.saddham;

import com.saddham.todolist.client.TodoListClient;
import com.saddham.todolist.json.parser.TodoListJsonParser;

import java.util.List;

public class App {
    public static void main(String[] args) {
        TodoListJsonParser todoListJsonParser = new TodoListJsonParser();
        List<String> todoList = todoListJsonParser.parseAsJsonString("db.json");

        TodoListClient todoListClient = new TodoListClient("<api server url>", "<api key holder>");
        todoListClient.createTodoList(todoList);
    }
}
