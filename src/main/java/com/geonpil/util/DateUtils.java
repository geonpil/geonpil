package com.geonpil.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static int calculateDDay(LocalDate endDate) {
        if (endDate == null) return -999;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }
}