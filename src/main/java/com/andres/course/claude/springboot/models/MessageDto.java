package com.andres.course.claude.springboot.models;

import java.time.LocalDateTime;

public record MessageDto(String message, LocalDateTime date, UserDto user) {
}
