package com.percy.messageboard.controller;

import com.percy.messageboard.dto.LoginForm;
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

@Controller
@RequiredArgsConstructor
public class AdminAuthController {

    private final UserService userService;

    @GetMapping("/admin/login")
    public String loginPage(@RequestParam(required = false) String redirect,
                            HttpSession session, Model model) {
        if (session.getAttribute(SessionKeys.ADMIN) != null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("redirect", redirect);
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult result,
                        @RequestParam(required = false) String redirect,
                        HttpServletRequest request,
                        HttpSession session,
                        Model model) {
        model.addAttribute("redirect", redirect);
        if (result.hasErrors()) {
            return "admin/login";
        }
        SessionUser admin = userService.authenticateAdmin(form.getEmail(), form.getPassword());
        if (admin == null) {
            model.addAttribute("error", "邮箱或密码错误，或该账号没有管理员权限");
            return "admin/login";
        }
        request.changeSessionId();
        session.setAttribute(SessionKeys.ADMIN, admin);
        return "redirect:" + RedirectUtils.safe(redirect, "/admin/users");
    }

    @PostMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
