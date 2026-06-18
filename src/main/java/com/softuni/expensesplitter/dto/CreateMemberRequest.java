package com.softuni.expensesplitter.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateMemberRequest {

    @NotBlank
    private String name;

    public CreateMemberRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
