package com.andres.course.claude.springboot.models;

public record UserDto(String name, String lastname, String email, int age) {

    @Override
    public String toString() {
        return """
                {"name":"%s","lastname":"%s","email":"%s","age":%d}"""
                .formatted(name, lastname, email, age);
    }
}
