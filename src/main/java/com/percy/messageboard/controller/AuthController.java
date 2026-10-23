package com.percy.messageboard.controller;

import com.percy.messageboard.dto.LoginForm;
import com.percy.messageboard.dto.RegisterForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.service.UserService;
import com.percy.messageboard.util.RedirectUtils;
import com.percy.messageboard.util.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/user/login")
    public String loginPage(@RequestParam(required = false) String redirect,
                            HttpSession session, Model model) {
        if (session.getAttribute(SessionKeys.USER) != null) {
            return "redirect:/";
        }
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/user/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult result,
                        @RequestParam(required = false) String redirect,
                        HttpServletRequest request,
                        HttpSession session,
                        Model model) {
        model.addAttribute("redirect", redirect);
        if (result.hasErrors()) {
            return "auth/login";
        }
        SessionUser user = userService.authenticate(form.getEmail(), form.getPassword());
        if (user == null) {
            model.addAttribute("error", "邮箱或密码错误");
            return "auth/login";
        }
        request.changeSessionId();
        session.setAttribute(SessionKeys.USER, user);
        return "redirect:" + RedirectUtils.safe(redirect, "/");
    }

    @GetMapping("/user/register")
    public String registerPage(HttpSession session, Model model) {
        if (session.getAttribute(SessionKeys.USER) != null) {
            return "redirect:/";
        }
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/user/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           HttpServletRequest request,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        SessionUser user;
        try {
            user = userService.register(form);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.taken", e.getMessage());
            return "auth/register";
        }
        request.changeSessionId();
        session.setAttribute(SessionKeys.USER, user);
        redirectAttributes.addFlashAttribute("success", "注册成功，欢迎加入");
        return "redirect:/";
    }

    @PostMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
