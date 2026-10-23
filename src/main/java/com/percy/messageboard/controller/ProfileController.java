package com.percy.messageboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.config.AppProperties;
import com.percy.messageboard.dto.MessageView;
import com.percy.messageboard.dto.ProfileForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.service.AvatarStorageService;
import com.percy.messageboard.service.MessageService;
import com.percy.messageboard.service.UserService;
import com.percy.messageboard.util.SessionKeys;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final MessageService messageService;
    private final AvatarStorageService avatarStorageService;
    private final AppProperties appProperties;

    @GetMapping("/user/profile")
    public String profile(HttpSession session, Model model) {
        SessionUser current = current(session);
        SessionUser user = userService.snapshot(current.getId());
        session.setAttribute(SessionKeys.USER, user);
        model.addAttribute("user", user);
        model.addAttribute("messageCount", messageService.countByUser(user.getId()));
        return "user/profile";
    }

    @GetMapping("/user/profile/edit")
    public String editPage(HttpSession session, Model model) {
        SessionUser user = current(session);
        model.addAttribute("profileForm", ProfileForm.of(user.getName(), user.getEmail()));
        return "user/edit";
    }

    @PostMapping("/user/profile/edit")
    public String edit(@Valid @ModelAttribute("profileForm") ProfileForm form,
                       BindingResult result,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/edit";
        }
        SessionUser current = current(session);
        try {
            SessionUser updated = userService.updateProfile(current.getId(), form);
            session.setAttribute(SessionKeys.USER, updated);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.taken", e.getMessage());
            return "user/edit";
        }
        redirectAttributes.addFlashAttribute("success", "资料已更新");
        return "redirect:/user/profile";
    }

    @GetMapping("/user/avatar")
    public String avatarPage(@RequestParam(required = false) String error,
                             HttpSession session, Model model) {
        if ("size".equals(error)) {
            model.addAttribute("error", "图片大小不能超过 2MB");
        }
        model.addAttribute("user", current(session));
        return "user/avatar";
    }

    @PostMapping("/user/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        SessionUser current = current(session);
        String oldAvatar = current.getAvatar();
        try {
            String filename = avatarStorageService.store(file);
            SessionUser updated = userService.updateAvatar(current.getId(), filename);
            session.setAttribute(SessionKeys.USER, updated);
            avatarStorageService.delete(oldAvatar);
            redirectAttributes.addFlashAttribute("success", "头像已更新");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "上传失败，请稍后重试");
        }
        return "redirect:/user/avatar";
    }

    @GetMapping("/user/messages")
    public String messages(@RequestParam(defaultValue = "1") long page,
                           HttpSession session,
                           Model model) {
        SessionUser current = current(session);
        Page<MessageView> messagePage =
                messageService.page(page, appProperties.getPageSize(), current.getId());
        model.addAttribute("page", messagePage);
        model.addAttribute("messages", messagePage.getRecords());
        model.addAttribute("messageCount", messagePage.getTotal());
        return "user/messages";
    }

    private SessionUser current(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionKeys.USER);
    }
}
