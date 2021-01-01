package com.anilaltunkan.security.controller;

import com.anilaltunkan.security.Exceptions.ResourceNotFoundException;
import com.anilaltunkan.security.dto.LoginRequest;
import com.anilaltunkan.security.dto.UserSummary;
import com.anilaltunkan.security.Interface.UserService;
import com.anilaltunkan.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/profile")
    public ResponseEntity<UserSummary> profileUser() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @RequestMapping("/list")
    public String viewHomePage(Model model) {
        List<User> Users = userService.listAll();
        model.addAttribute("Users", Users);

        return "UserViews/list";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditUserPage(@PathVariable(name = "id") int id) throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("UserViews/edit");
        User user = userService.get(id);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute("user") User user) throws ResourceNotFoundException {
        User _user = userService.get(user.getId());
        _user.setUserName(user.getUserName());
        _user.setEmail(user.getEmail());

        userService.save(_user);

        return "redirect:/user/list";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") int id) {
        userService.delete(id);
        return "redirect:/user/list";
    }

}