package com.percy.messageboard.controller;

import com.percy.messageboard.dto.MessageForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.service.MessageService;
import com.percy.messageboard.util.IpUtils;
import com.percy.messageboard.util.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public String create(@Valid @ModelAttribute("messageForm") MessageForm form,
                         BindingResult result,
                         HttpSession session,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            String error = result.getAllErrors().isEmpty()
                    ? "留言内容不合法"
                    : result.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("draftContent", form.getContent());
            return "redirect:/";
        }
        SessionUser current = (SessionUser) session.getAttribute(SessionKeys.USER);
        messageService.create(current.getId(), form.getContent(), IpUtils.clientIp(request));
        redirectAttributes.addFlashAttribute("success", "留言发布成功");
        return "redirect:/";
    }

    @PostMapping("/user/messages/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "1") long page,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        SessionUser current = (SessionUser) session.getAttribute(SessionKeys.USER);
        messageService.deleteOwn(id, current.getId());
        redirectAttributes.addFlashAttribute("success", "留言已删除");
        return "redirect:/user/messages?page=" + Math.max(page, 1);
    }
}
