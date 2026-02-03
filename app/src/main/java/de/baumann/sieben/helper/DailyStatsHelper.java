package de.baumann.sieben.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DailyStatsHelper {

    private static final String DAILY_STATS_PREFIX = "daily_stats_";

    /**
     * Increment the daily exercise count for today
     */
    public static void incrementTodayCount(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String today = getTodayKey();
        int currentCount = sharedPref.getInt(today, 0);
        sharedPref.edit().putInt(today, currentCount + 1).apply();
    }

    /**
     * Get the count for a specific date
     */
    public static int getCountForDate(Context context, String dateKey) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(dateKey, 0);
    }

    /**
     * Get the weekly stats starting from last Sunday
     * Returns a Map with day names as keys and exercise counts as values
     */
    public static Map<String, Integer> getWeeklyStats(Context context) {
        Map<String, Integer> weeklyStats = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        // Move to last Sunday (or today if it's Sunday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSunday = dayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromSunday);

        // Collect stats for each day of the week (Sunday to Saturday)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            String dateKey = getDateKey(calendar);
            String dayName = dayFormat.format(calendar.getTime());
            int count = getCountForDate(context, dateKey);
            weeklyStats.put(dayName, count);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return weeklyStats;
    }

    /**
     * Get the weekly stats with full date information
     * Returns a Map with formatted date strings as keys and exercise counts as values
     */
    public static Map<String, Integer> getWeeklyStatsWithDates(Context context) {
        Map<String, Integer> weeklyStats = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        // Move to last Sunday (or today if it's Sunday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSunday = dayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromSunday);

        // Collect stats for each day of the week (Sunday to Saturday)
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            String dateKey = getDateKey(calendar);
            String displayDate = displayFormat.format(calendar.getTime());
            int count = getCountForDate(context, dateKey);
            weeklyStats.put(displayDate, count);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return weeklyStats;
    }

    /**
     * Get today's date key
     */
    private static String getTodayKey() {
        Calendar calendar = Calendar.getInstance();
        return getDateKey(calendar);
    }

    /**
     * Get date key for a specific calendar instance
     */
    private static String getDateKey(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return DAILY_STATS_PREFIX + sdf.format(calendar.getTime());
    }

    /**
     * Get the total count for the current week
     */
    public static int getWeeklyTotal(Context context) {
        Map<String, Integer> weeklyStats = getWeeklyStats(context);
        int total = 0;
        for (Integer count : weeklyStats.values()) {
            total += count;
        }
        return total;
    }

    /**
     * Get Year-to-Date (YTD) statistics by week
     * Returns a Map with week number as key and total exercises for that week as value
     */
    public static Map<Integer, Integer> getYTDWeeklyStats(Context context) {
        Map<Integer, Integer> ytdStats = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        
        // Get current week of year
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);
        
        // Start from week 1 of current year
        calendar.set(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.YEAR, currentYear);
        
        // Iterate through each week up to current week
        for (int week = 1; week <= currentWeek; week++) {
            int weekTotal = 0;
            
            // Sum up exercises for all 7 days of this week
            for (int day = 0; day < 7; day++) {
                String dateKey = getDateKey(calendar);
                weekTotal += getCountForDate(context, dateKey);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            ytdStats.put(week, weekTotal);
        }
        
        return ytdStats;
    }

    /**
     * Get the total count for the entire year to date
     */
    public static int getYTDTotal(Context context) {
        Map<Integer, Integer> ytdStats = getYTDWeeklyStats(context);
        int total = 0;
        for (Integer count : ytdStats.values()) {
            total += count;
        }
        return total;
    }
}
