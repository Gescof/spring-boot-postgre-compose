package com.gescof.springbootpostgrecompose.models;

import lombok.Builder;

@Builder
public record CustomerRequest(
        String name,
        String email,
        Integer age
) {
}
