package com.geonpil.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DdayUtils {
    public static int calculateDDay(LocalDate endDate) {
        if (endDate == null) return -999;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }


    public static String getLabel(int dDay) {
        if (dDay == -999) {
            return "마감 미정";
        } else if (dDay > 0) {
            return "D-" + dDay;
        } else if (dDay == 0) {
            return "오늘 마감";
        } else {
            return "마감";
        }
    }
}