package com.github.artemis.the.gr8.playerstats.msg.msgutils;

import com.github.artemis.the.gr8.playerstats.enums.Unit;

import java.text.DecimalFormat;

/** A utility class that formats statistic numbers into something more readable.
 It transforms numbers of {@link Unit.Type} Time, Damage, and Distance into numbers
 that are easier to understand (for example: from ticks to hours) and adds commas
 to break up large numbers.*/
public final class NumberFormatter {

    private final DecimalFormat format;

    public NumberFormatter() {
        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    /** Turns the input number into a more readable format depending on its type
     (number-of-times, time-, damage- or distance-based) according to the
     corresponding config settings, and adds commas in groups of 3.*/
    public String formatNumber(long number) {
        return format.format(number);
    }

    /** The unit of damage-based statistics is half a heart by default.
     This method turns the number into hearts. */
    public String formatDamageNumber(long number, Unit statUnit) {  //7 statistics
        if (statUnit == Unit.HEART) {
            return format.format(Math.round(number / 2.0));
        } else {
            return format.format(number);
        }
    }

    /** The unit of distance-based statistics is cm by default. This method turns it into blocks by default,
     and turns it into km or leaves it as cm otherwise, depending on the config settings. */
    public String formatDistanceNumber(long number, Unit statUnit) {  //15 statistics
        switch (statUnit) {
            case CM -> {
                return format.format(number);
            }
            case MILE -> {
                return format.format(Math.round(number / 160934.4));  //to get from CM to Miles
            }
            case KM -> {
                return format.format(Math.round(number / 100000.0));  //divide by 100 to get M, divide by 1000 to get KM
            }
            default -> {
                return format.format(Math.round(number / 100.0));
            }
        }
    }

    /** The unit of time-based statistics is ticks by default.
     @return a String with the form "1D 2H 3M 4S" (depending on the Unit range selected)*/
    public String formatTimeNumber(long number, Unit bigUnit, Unit smallUnit) {  //5 statistics
        if (number == 0) {
            return "-";
        }
        if (bigUnit == Unit.TICK && smallUnit == Unit.TICK || bigUnit == Unit.NUMBER || smallUnit == Unit.NUMBER) {
            return format.format(number);
        }

        StringBuilder output = new StringBuilder();
        double max = bigUnit.getSeconds();
        double min = smallUnit.getSeconds();
        double leftover = number / 20.0;

        if (isInRange(max, 86400, min) && leftover >= 86400) {
            double days = Math.floor(leftover / 86400);
            leftover = leftover % (86400);
            if (smallUnit == Unit.DAY) {
                if (leftover >= 43200) {
                    days++;
                }
                return output.append(format.format(days))
                        .append("d").toString();
            }
            if (days == 0) {
                output.append("0d");
            } else {
                output.append(format.format(days))
                        .append("d");
            }
        }
        if (isInRange(max, 3600, min)) {
            if (output.toString().contains("d")) {
                output.append(" ");
            }
            double hours = Math.floor(leftover / 60 / 60);
            leftover = leftover % (60 * 60);
            if (smallUnit == Unit.HOUR) {
                if (leftover >= 1800) {
                    hours++;
                }
                return output.append(format.format(hours))
                        .append("h").toString();
            }
            if (hours == 0) {
                output.append("0h");
            } else {
                output.append(format.format(hours))
                        .append("h");
            }
        }
        if (isInRange(max, 60, min)) {
            if (output.toString().contains("h")) {
                output.append(" ");
            }
            double minutes = Math.floor(leftover / 60);
            leftover = leftover % 60;
            if (smallUnit == Unit.MINUTE) {
                if (leftover >= 30) {
                    minutes++;
                }
                return output.append(format.format(minutes))
                        .append("m").toString();
            }
            if (minutes == 0) {
                output.append("0m");
            } else {
                output.append(format.format(minutes))
                        .append("m");
            }
        }
        if (isInRange(max,1, min) && leftover > 0) {
            if (output.toString().contains("m")) {
                output.append(" ");
            }
            double seconds = Math.ceil(leftover);
            output.append(format.format(seconds))
                    .append("s");
        }
        return output.toString();
    }

    private boolean isInRange(double bigUnit, double unitToEvaluate, double smallUnit) {
        return bigUnit >= unitToEvaluate && unitToEvaluate >= smallUnit;
    }
}