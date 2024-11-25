package com.ezen.boot_JPA.controller;

import com.ezen.boot_JPA.dto.UserDTO;
import com.ezen.boot_JPA.service.CommentService;
import com.ezen.boot_JPA.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@RequestMapping("/user/*")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public void join(){}

    @PostMapping("/join")
    public String join(UserDTO userDTO){
        log.info(">>> userDTO >>> {}", userDTO);
        // password 암호화
        userDTO.setPwd(passwordEncoder.encode(userDTO.getPwd()));
        String email = userService.register(userDTO);
        log.info(">>> email >>> {}", email);
        return (email == null ? "/user/join" : "/index");
    }

    @GetMapping("/login")
    public void login(@RequestParam(value = "error", required = false) String error,
                      @RequestParam(value = "exception", required = false) String exception,
                      Model model){
        /* 에러의 예외값을 담아 화면으로 전달 */
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
    }

    @GetMapping("/list")
    public void list(Model model){
        model.addAttribute("userList", userService.getList());
    }

    @GetMapping("/modify")
    public void modfiy(Principal principal, Model model){
        model.addAttribute("userDTO", userService.selectEmail(principal.getName()));
    }

    @PostMapping("/modify")
    public String modify(UserDTO userDTO){
        if(userDTO.getPwd().length() > 0){
            userDTO.setPwd(passwordEncoder.encode(userDTO.getPwd()));
        }
        String email = userService.modify(userDTO);
        return "redirect:/user/logout";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("email") String email){
        userService.remove(email);
        return "redirect:/user/logout";
    }
}
