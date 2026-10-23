package com.percy.messageboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.config.AppProperties;
import com.percy.messageboard.dto.MessageView;
import com.percy.messageboard.service.GeoIpService;
import com.percy.messageboard.service.MessageService;
import com.percy.messageboard.service.UserService;
import com.percy.messageboard.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MessageService messageService;
    private final UserService userService;
    private final GeoIpService geoIpService;
    private final AppProperties appProperties;

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "1") long page,
                       Model model,
                       HttpServletRequest request) {
        Page<MessageView> messagePage =
                messageService.page(page, appProperties.getPageSize(), null);
        String currentIp = IpUtils.clientIp(request);
        model.addAttribute("page", messagePage);
        model.addAttribute("messages", messagePage.getRecords());
        model.addAttribute("messageCount", messagePage.getTotal());
        model.addAttribute("userCount", userService.count());
        model.addAttribute("currentIp", currentIp);
        model.addAttribute("currentProvince", geoIpService.resolveProvince(currentIp));
        return "index";
    }
}
