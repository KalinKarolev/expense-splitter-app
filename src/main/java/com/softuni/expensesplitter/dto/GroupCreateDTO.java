package com.softuni.expensesplitter.dto;

import jakarta.validation.constraints.NotBlank;

public class GroupCreateDTO {

    @NotBlank
    private String name;

    private String description;

    public GroupCreateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
