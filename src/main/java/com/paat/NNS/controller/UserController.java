package com.paat.NNS.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @GetMapping
    @PreAuthorize("hasAuthority('GET_CURRENT_USER')")
    public void getCurrentUser() {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/login")
    public void login() {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GET_USER', 'DELETE_USER', 'ADD_USER')")
    public void getUser() {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
