package com.geonpil.mapper.bugReport;

import com.geonpil.domain.bugReport.BugReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BugReportMapper {

    @Insert("""
        INSERT INTO bug_report (nickname, content, ip_address)
        VALUES (#{nickname}, #{content}, #{ipAddress})
    """)
    void insertBugReport(BugReport bugReport);


    @Select("""
    SELECT id, nickname, content, ip_address AS ipAddress, created_at AS createdAt 
    FROM bug_report
    ORDER BY created_at DESC
""")
    List<BugReport> findAll();
}