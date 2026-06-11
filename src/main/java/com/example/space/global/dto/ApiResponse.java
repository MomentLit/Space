package com.example.space.global.dto;

public record ApiResponse<T>(
        String message,
        T data
) {
}