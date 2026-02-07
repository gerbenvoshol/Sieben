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
    
    // MET (Metabolic Equivalent of Task) values for the 7-minute workout exercises
    // Based on the Compendium of Physical Activities and research on HICT
    // Source: Ainsworth BE et al. (2011) Compendium of Physical Activities
    private static final double MET_JUMPING_JACKS = 8.0;      // Vigorous calisthenics
    private static final double MET_WALL_SIT = 5.0;           // Isometric exercise
    private static final double MET_PUSH_UPS = 8.0;           // Vigorous effort
    private static final double MET_ABDOMINAL_CRUNCH = 4.5;   // Moderate calisthenics
    private static final double MET_STEP_UP = 8.0;            // Vigorous step exercise
    private static final double MET_SQUAT = 5.5;              // Moderate resistance
    private static final double MET_TRICEPS_DIPS = 5.0;       // Moderate resistance
    private static final double MET_PLANK = 4.0;              // Isometric core
    private static final double MET_HIGH_KNEES = 10.0;        // Running in place vigorous
    private static final double MET_LUNGE = 4.0;              // Moderate resistance
    private static final double MET_PUSH_UP_ROTATION = 8.5;   // Advanced variation
    private static final double MET_SIDE_PLANK = 4.5;         // Isometric core
    
    // Average MET for the 7-minute workout circuit
    // (8.0 + 5.0 + 8.0 + 4.5 + 8.0 + 5.5 + 5.0 + 4.0 + 10.0 + 4.0 + 8.5 + 4.5) / 12 = 6.25
    private static final double AVERAGE_MET_VALUE = 6.25;
    
    // Average adult weight in kg (used when user weight is not available)
    // Based on global average of ~70kg for adults
    private static final double DEFAULT_WEIGHT_KG = 70.0;
    
    // Calorie calculation: Calories = MET × Weight(kg) × Time(hours)
    // For HICT (High-Intensity Circuit Training) like the 7-minute workout,
    // we add a 15% bonus for EPOC (Excess Post-exercise Oxygen Consumption) effect
    // EPOC represents continued calorie burn after high-intensity exercise
    private static final double EPOC_MULTIPLIER = 1.15;
    
    // Final calories per minute calculation:
    // Base formula: Calories = MET × Weight(kg) × Time(hours)
    // For 1 minute: 6.25 MET × 70kg × (1/60 hours) = 7.29 cal/min
    // With EPOC: 7.29 × 1.15 = 8.39 cal/min
    // Note: This is a rough estimate. Actual calorie burn varies significantly based on
    // individual factors such as weight, age, gender, fitness level, and exercise intensity.
    // The 7-minute workout typically burns between 50-100 calories (7-14 cal/min).
    private static final double CALORIES_PER_MINUTE = 
            (AVERAGE_MET_VALUE * DEFAULT_WEIGHT_KG / 60.0) * EPOC_MULTIPLIER;

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
     * This method calculates calories based on actual exercises performed,
     * using exercise-specific MET values rather than a simple average.
     */
    public static int getWeeklyCalories(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        // Get time spent on each exercise (in milliseconds)
        long ex1Time = sharedPref.getInt("ex1_time", 0);
        long ex2Time = sharedPref.getInt("ex2_time", 0);
        long ex3Time = sharedPref.getInt("ex3_time", 0);
        long ex4Time = sharedPref.getInt("ex4_time", 0);
        long ex5Time = sharedPref.getInt("ex5_time", 0);
        long ex6Time = sharedPref.getInt("ex6_time", 0);
        long ex7Time = sharedPref.getInt("ex7_time", 0);
        long ex8Time = sharedPref.getInt("ex8_time", 0);
        long ex9Time = sharedPref.getInt("ex9_time", 0);
        long ex10Time = sharedPref.getInt("ex10_time", 0);
        long ex11Time = sharedPref.getInt("ex11_time", 0);
        long ex12Time = sharedPref.getInt("ex12_time", 0);
        
        // Calculate calories for each exercise using its specific MET value
        // Formula: Calories = MET × Weight(kg) × Time(hours) × EPOC_MULTIPLIER
        double calories = 0;
        calories += calculateExerciseCalories(ex1Time, MET_JUMPING_JACKS);
        calories += calculateExerciseCalories(ex2Time, MET_WALL_SIT);
        calories += calculateExerciseCalories(ex3Time, MET_PUSH_UPS);
        calories += calculateExerciseCalories(ex4Time, MET_ABDOMINAL_CRUNCH);
        calories += calculateExerciseCalories(ex5Time, MET_STEP_UP);
        calories += calculateExerciseCalories(ex6Time, MET_SQUAT);
        calories += calculateExerciseCalories(ex7Time, MET_TRICEPS_DIPS);
        calories += calculateExerciseCalories(ex8Time, MET_PLANK);
        calories += calculateExerciseCalories(ex9Time, MET_HIGH_KNEES);
        calories += calculateExerciseCalories(ex10Time, MET_LUNGE);
        calories += calculateExerciseCalories(ex11Time, MET_PUSH_UP_ROTATION);
        calories += calculateExerciseCalories(ex12Time, MET_SIDE_PLANK);
        
        return (int) Math.round(calories);
    }
    
    /**
     * Calculate calories burned for a specific exercise
     * @param timeMs Time spent on exercise in milliseconds
     * @param metValue MET value for the exercise
     * @return Calories burned
     */
    private static double calculateExerciseCalories(long timeMs, double metValue) {
        if (timeMs <= 0) {
            return 0;
        }
        // Convert milliseconds to hours
        double timeHours = timeMs / (1000.0 * 60.0 * 60.0);
        // Apply formula: Calories = MET × Weight(kg) × Time(hours) × EPOC_MULTIPLIER
        return metValue * DEFAULT_WEIGHT_KG * timeHours * EPOC_MULTIPLIER;
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
