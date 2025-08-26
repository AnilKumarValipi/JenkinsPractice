package com.example;

public class App {
    public static String greet(String who) {
        return "Hello, " + who + "!";
    }

    public static void main(String[] args) {
        System.out.println(greet("World"));
    }
}
