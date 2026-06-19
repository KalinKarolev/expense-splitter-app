package com.softuni.expensesplitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/groups/{id}")
    public String getGroupPage(@PathVariable Long id) {
        return "forward:/group.html";
    }
}
