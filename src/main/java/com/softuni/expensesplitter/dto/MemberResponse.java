package com.softuni.expensesplitter.dto;

public class MemberResponse {

    private Long id;
    private String name;
    private Long groupId;

    public MemberResponse() {
    }

    public MemberResponse(Long id, String name, Long groupId) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
