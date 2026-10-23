package com.percy.messageboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.config.AppProperties;
import com.percy.messageboard.dto.AdminUserForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.dto.UserRow;
import com.percy.messageboard.entity.User;
import com.percy.messageboard.service.MessageService;
import com.percy.messageboard.service.UserService;
import com.percy.messageboard.util.SessionKeys;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final MessageService messageService;
    private final AppProperties appProperties;

    @GetMapping("/admin/users")
    public String users(@RequestParam(defaultValue = "1") long page, Model model) {
        Page<UserRow> userPage = userService.pageUsers(page, appProperties.getPageSize());
        model.addAttribute("page", userPage);
        model.addAttribute("users", userPage.getRecords());
        model.addAttribute("userCount", userPage.getTotal());
        model.addAttribute("messageCount", messageService.count());
        return "admin/users";
    }

    @GetMapping("/admin/users/new")
    public String newUser(Model model) {
        model.addAttribute("adminUserForm", new AdminUserForm());
        populateFormChrome(model, false, null);
        return "admin/user-form";
    }

    @PostMapping("/admin/users")
    public String create(@Valid @ModelAttribute("adminUserForm") AdminUserForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!result.hasErrors() && !StringUtils.hasText(form.getPassword())) {
            result.rejectValue("password", "password.required", "请为新管理员设置密码");
        }
        if (result.hasErrors()) {
            populateFormChrome(model, false, null);
            return "admin/user-form";
        }
        try {
            userService.createAdmin(form);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.taken", e.getMessage());
            populateFormChrome(model, false, null);
            return "admin/user-form";
        }
        redirectAttributes.addFlashAttribute("success", "管理员已添加");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.require(id);
        model.addAttribute("adminUserForm", AdminUserForm.of(user.getName(), user.getEmail()));
        populateFormChrome(model, true, user);
        return "admin/user-form";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("adminUserForm") AdminUserForm form,
                             BindingResult result,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return editView(id, model);
        }
        try {
            userService.adminUpdate(id, form);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.taken", e.getMessage());
            return editView(id, model);
        }
        SessionUser admin = (SessionUser) session.getAttribute(SessionKeys.ADMIN);
        if (admin != null && admin.getId().equals(id)) {
            session.setAttribute(SessionKeys.ADMIN, userService.snapshot(id));
        }
        redirectAttributes.addFlashAttribute("success", "用户资料已更新");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUser admin = (SessionUser) session.getAttribute(SessionKeys.ADMIN);
        try {
            userService.deleteUser(id, admin.getId());
            redirectAttributes.addFlashAttribute("success", "用户已删除");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private String editView(Long id, Model model) {
        populateFormChrome(model, true, userService.require(id));
        return "admin/user-form";
    }

    private void populateFormChrome(Model model, boolean editing, User editingUser) {
        model.addAttribute("editing", editing);
        model.addAttribute("editingUser", UserRow.from(editingUser));
        model.addAttribute("pageTitle", editing ? "编辑用户" : "新增管理员");
        model.addAttribute("navActive", editing ? "users" : "new");
        model.addAttribute("formAction", editing
                ? "/admin/users/" + editingUser.getId() + "/edit"
                : "/admin/users");
    }
}
