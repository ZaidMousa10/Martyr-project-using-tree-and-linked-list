package com.example.secondproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MDate implements Comparable<MDate> {
    private String date;
    private SLinkedList<Martyr> martyr;

    public MDate(String date) {
        this.date = date;
        this.martyr = new SLinkedList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SLinkedList<Martyr> getMartyr() {
        return martyr;
    }

    public void setMartyr(SLinkedList<Martyr> martyr) {
        this.martyr = martyr;
    }



    @Override
    public int compareTo(MDate other) {
        // Parse dates and compare based on date values
        Date thisDate = parseDate(this.date);
        Date otherDate = parseDate(other.date);

        // Check if parsing was successful
        if (thisDate == null || otherDate == null) {
            throw new IllegalArgumentException("Failed to parse dates for comparison");
        }

        return thisDate.compareTo(otherDate);
    }

    private Date parseDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return  date;
    }


}
