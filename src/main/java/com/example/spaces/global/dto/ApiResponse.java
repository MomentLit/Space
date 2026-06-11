package com.example.spaces.global.dto;

public record ApiResponse<T>(
        String message,
        T data
) {
}