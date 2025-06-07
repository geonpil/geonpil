package com.geonpil.service.bugReport;

import com.geonpil.domain.bugReport.BugReport;
import com.geonpil.mapper.bugReport.BugReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BugReportService {

    private final BugReportMapper bugReportMapper;

    public void submitBugReport(BugReport bugReport) {
        bugReportMapper.insertBugReport(bugReport);
    }

    public List<BugReport> getAllReports() {
        return bugReportMapper.findAll();
    }
}