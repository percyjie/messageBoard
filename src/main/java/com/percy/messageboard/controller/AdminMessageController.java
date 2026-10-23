package com.percy.messageboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.config.AppProperties;
import com.percy.messageboard.dto.MessageView;
import com.percy.messageboard.entity.User;
import com.percy.messageboard.service.MessageService;
import com.percy.messageboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminMessageController {

    private final UserService userService;
    private final MessageService messageService;
    private final AppProperties appProperties;

    @GetMapping("/admin/users/{id}/messages")
    public String messages(@PathVariable Long id,
                           @RequestParam(defaultValue = "1") long page,
                           Model model) {
        User user = userService.require(id);
        Page<MessageView> messagePage =
                messageService.page(page, appProperties.getPageSize(), id);
        model.addAttribute("user", user);
        model.addAttribute("page", messagePage);
        model.addAttribute("messages", messagePage.getRecords());
        model.addAttribute("baseUrl", "/admin/users/" + id + "/messages");
        return "admin/user-messages";
    }

    @PostMapping("/admin/users/{uid}/messages/{mid}/delete")
    public String delete(@PathVariable Long uid,
                         @PathVariable Long mid,
                         @RequestParam(defaultValue = "1") long page,
                         RedirectAttributes redirectAttributes) {
        messageService.deleteAsAdmin(mid, uid);
        redirectAttributes.addFlashAttribute("success", "留言已删除");
        return "redirect:/admin/users/" + uid + "/messages?page=" + Math.max(page, 1);
    }
}
