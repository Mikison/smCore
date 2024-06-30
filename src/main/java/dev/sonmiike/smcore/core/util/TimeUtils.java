package dev.sonmiike.smcore.core.util;

import java.time.Duration;

public class TimeUtils {

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);

        long days = absSeconds / 86400;
        long hours = (absSeconds % 86400) / 3600;
        long minutes = ((absSeconds % 86400) % 3600) / 60;
        long secs = ((absSeconds % 86400) % 3600) % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" days, ");
        }
        if (hours > 0 || days > 0) {
            result.append(hours).append(" hours, ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            result.append(minutes).append(" minutes, ");
        }
        result.append(secs).append(" seconds");

        return result.toString();
    }
}
