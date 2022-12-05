package com.gescof.springbootpostgrecompose.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
}
