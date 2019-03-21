package com.wirelabs.common.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Jaison Pereira - 22 de dez de 2016 Classe de utilitarios que Manipula
 *         LocalDateTime -LocalTime -LocalDate Synchronized para concorrencia
 */
@Component
public class DateUtil {

    public static final String DATE_MASK = "dd/MMM/yyyy";
    public static final String DATABASE_DATE_MASK = "yyyy-MM-dd HH:mm:ss";
    public static final String PRESENTATION_DATE_MASK = "dd/MM/yyyy";
    public static final String DATE_TIME_MASK = "dd/MM/yyyy HH:mm:ss";
    public static final String DAY_MASK = "dd";
    public static final String MES_REFERENCIA_MASK = "MMyyyy";

    /**
     * Responsavel por parsear de String para LocalDateTime
     *
     * @param value
     * @param format
     * @return
     */
    public LocalDateTime parse(final String value, final String format) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(value, dtf);
    }

    public LocalDateTime parseDate(final String value) {
        return parse(value, PRESENTATION_DATE_MASK);
    }

    public LocalDateTime parseDateTime(final String value) {
        return parse(value, DATE_TIME_MASK);
    }

    public String parseToString(Calendar date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_MASK);

        return sdf.format(date);
    }

    public String parseToString(Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_MASK);

        return sdf.format(date);
    }

    public Date removeDays(Date date, int days) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DAY_OF_MONTH, -days);

        return gc.getTime();
    }

    public Date removeMonths(Date date, int months) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.MONTH, -months);

        return gc.getTime();
    }

    public Date resetHourMinuteSecondMilli(Date date) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);

        return gc.getTime();
    }

    public Date setDay(Date date, int day) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, day);

        return gc.getTime();
    }

    public String toStringDate(LocalDateTime date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PRESENTATION_DATE_MASK);
        return date.format(dtf);
    }

    public String toStringDateTime(LocalDateTime date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_MASK);
        return date.format(dtf);
    }

}
