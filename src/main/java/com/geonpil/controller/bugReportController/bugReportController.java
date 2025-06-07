package com.geonpil.controller.bugReportController;

import com.geonpil.domain.Book;
import com.geonpil.domain.Review;
import com.geonpil.domain.bugReport.BugReport;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.bugReport.BugReportService;
import com.geonpil.service.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class bugReportController {

    private final BugReportService bugReportService;

    @GetMapping("/bug-report")
    public String getBookDetail(Model model) {
        List<BugReport> reports = bugReportService.getAllReports();
        model.addAttribute("comments", reports);
        return "bugReport/bugReport"; // bug/report.html
    }


    @PostMapping("/bug-report/comment")
    public String submitBug(@ModelAttribute BugReport dto, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        dto.setIpAddress(ip);
        bugReportService.submitBugReport(dto);
        return "redirect:/bug-report";
    }
}
