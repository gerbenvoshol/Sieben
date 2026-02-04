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
    private static final String DAILY_TIME_PREFIX = "daily_time_";
    // Approximate calories burned per minute of exercise
    // Note: This is a rough estimate. Actual calorie burn varies significantly based on
    // individual factors such as weight, age, gender, fitness level, and exercise intensity.
    // The 7-minute workout typically burns between 50-100 calories (7-14 cal/min).
    private static final double CALORIES_PER_MINUTE = 10.0;

    /**
     * Increment the daily exercise count for today
     * @param context The application context
     * @param durationMs The actual workout duration in milliseconds
     */
    public static void incrementTodayCount(Context context, long durationMs) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String today = getTodayKey();
        int currentCount = sharedPref.getInt(today, 0);
        sharedPref.edit().putInt(today, currentCount + 1).apply();
        
        // Also increment workout time with actual duration
        String timeKey = getTimeKey(today);
        long currentTime = sharedPref.getLong(timeKey, 0);
        sharedPref.edit().putLong(timeKey, currentTime + durationMs).apply();
    }
    
    /**
     * Get time key from date key
     */
    private static String getTimeKey(String dateKey) {
        return dateKey.replace(DAILY_STATS_PREFIX, DAILY_TIME_PREFIX);
    }
    
    /**
     * Get the workout time in milliseconds for a specific date
     */
    public static long getTimeForDate(Context context, String dateKey) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String timeKey = getTimeKey(dateKey);
        return sharedPref.getLong(timeKey, 0);
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
     * Get the total workout time in milliseconds for the current week
     */
    public static long getWeeklyTotalTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        
        // Move to last Sunday (or today if it's Sunday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSunday = dayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromSunday);
        
        long totalTime = 0;
        for (int i = 0; i < 7; i++) {
            String dateKey = getDateKey(calendar);
            totalTime += getTimeForDate(context, dateKey);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return totalTime;
    }
    
    /**
     * Get estimated calories burned for the current week
     */
    public static int getWeeklyCalories(Context context) {
        long totalTimeMs = getWeeklyTotalTime(context);
        double totalMinutes = totalTimeMs / (60.0 * 1000.0);
        return (int) Math.round(totalMinutes * CALORIES_PER_MINUTE);
    }
    
    /**
     * Get daily exercise counts for the current week (for chart)
     * Returns an array of 7 integers (Sunday to Saturday)
     */
    public static int[] getWeeklyDailyCounts(Context context) {
        int[] dailyCounts = new int[7];
        Calendar calendar = Calendar.getInstance();
        
        // Move to last Sunday (or today if it's Sunday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSunday = dayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromSunday);
        
        for (int i = 0; i < 7; i++) {
            String dateKey = getDateKey(calendar);
            dailyCounts[i] = getCountForDate(context, dateKey);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return dailyCounts;
    }
    
    /**
     * Get the average daily exercises for the current week
     */
    public static float getWeeklyAverage(Context context) {
        int total = getWeeklyTotal(context);
        return total / 7.0f;
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
        
        // Iterate through each week up to current week
        for (int week = 1; week <= currentWeek; week++) {
            int weekTotal = 0;
            
            // Set calendar to the start of this week (Sunday)
            Calendar weekCalendar = Calendar.getInstance();
            weekCalendar.set(Calendar.YEAR, currentYear);
            weekCalendar.set(Calendar.WEEK_OF_YEAR, week);
            weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            
            // Sum up exercises for all 7 days of this week
            for (int day = 0; day < 7; day++) {
                String dateKey = getDateKey(weekCalendar);
                weekTotal += getCountForDate(context, dateKey);
                weekCalendar.add(Calendar.DAY_OF_YEAR, 1);
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
