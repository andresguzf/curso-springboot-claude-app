package com.andres.course.claude.springboot.models;

import java.time.LocalDateTime;

public record MessageHeadersDto(String message, LocalDateTime date, UserDto user, String username, String accessToken) {
}
