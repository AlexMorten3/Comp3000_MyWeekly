package com.example.myweekly_app.helper;

public class TimeConverters {

    // Convert "1300" to 1300
    public static int convertTimeStringToInt(String timeString) {
        try {
            return Integer.parseInt(timeString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getTimeOfDay(int dayMinutes) {

        int hours = dayMinutes / 60;

        if (hours < 12) {
            return 0;
        } else if (hours < 17) {
            return 1;
        } else {
            return 2;
        }

    }

    public static boolean isValidTimeFormat(String time) {
        String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
        return time.matches(regex);
    }

    // Convert "09:30" to "0930"
    public static String convertTimeToHHMM(String time) {
        return time.replace(":", "");
    }


    // Convert time integer (e.g., 1300) to formatted time string (e.g., "1:00 PM")
    public static String convertTimeIntToFormat(int timeInt) {
        String timeString = String.valueOf(timeInt);
        if (timeString.length() == 4) {
            return timeString.substring(0, 1) + ":" + timeString.substring(1, 3) +
                    (timeInt < 1200 ? " AM" : " PM");
        }
        return "";
    }

    // convert minutes to HHMM
    public static String convertMinutesToHHMM(int minutes) {
        int hours = minutes / 60;
        int remainderMinutes = minutes % 60;

        String hourString = String.format("%02d", hours); // Ensure two digits for hours
        String minuteString = String.format("%02d", remainderMinutes); // Ensure two digits for minutes

        String hhmm = hourString + minuteString;

        return hhmm;
    }

    public static int convertTimeToMinutes(String time) {
        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(2));
        return hours * 60 + minutes;
    }

    public static int convertStringTimeToMinutes(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        String[] parts = time.split(":|\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        int hours = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].trim());
        String period = parts[2].trim();

        if (period.equalsIgnoreCase("PM") && hours != 12) {
            hours += 12;
        } else if (period.equalsIgnoreCase("AM") && hours == 12) {
            hours = 0;
        }

        return hours * 60 + minutes;
    }

    // Convert "1300" to "1:00 PM"
    public static String convertTimeStringToFormat(String timeInt) {
        if (timeInt != null && timeInt.length() == 4) {
            String hourStr = timeInt.substring(0, 2);
            String minuteStr = timeInt.substring(2);

            int hour = Integer.parseInt(hourStr);

            String period = (hour < 12) ? "AM" : "PM";

            if (hour > 12) {
                hour -= 12;
            } else if (hour == 0) {
                hour = 12;
            }

            return hour + ":" + minuteStr + " " + period;
        }
        return "";
    }

    public static int convertFormattedToMinutes(String formattedTime) {
        try {
            String[] timeParts = formattedTime.split(" ");
            String time = timeParts[0];
            String period = timeParts[1];

            String[] hourAndMinute = time.split(":");
            int hours = Integer.parseInt(hourAndMinute[0]);
            int minutes = Integer.parseInt(hourAndMinute[1]);

            if (period.equalsIgnoreCase("PM") && hours != 12) {
                hours += 12;
            } else if (period.equalsIgnoreCase("AM") && hours == 12) {
                hours = 0;
            }

            int totalMinutes = (hours * 60) + minutes;
            return totalMinutes;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Convert "9:30 AM" to "09:00"
    public static String convertFormattedToEntryTimeString(String formattedTime) {
        try {
            String[] timeParts = formattedTime.split(" ");
            String time = timeParts[0];

            String[] hourAndMinute = time.split(":");
            int hours = Integer.parseInt(hourAndMinute[0]);
            int minutes = Integer.parseInt(hourAndMinute[1]);

            String formattedHours = String.format("%02d", hours);
            String formattedMinutes = String.format("%02d", minutes);

            return formattedHours + ":" + formattedMinutes;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convertDatabaseTypeToEntryTimeString(String time) {
        if (time == null || time.length() != 4) {
            return "";
        }

        String hours = time.substring(0, 2);
        String minutes = time.substring(2);

        String formattedTime = hours + ":" + minutes;

        return formattedTime;
    }


}
