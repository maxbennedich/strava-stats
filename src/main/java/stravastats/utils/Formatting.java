package stravastats.utils;

public class Formatting {
    public static String formatSeconds(double seconds) {
        int min = (int)Math.floor(seconds / 60);
        int sec = (int)(seconds - min*60 + 0.5);
        if (sec == 60) {
            sec = 0;
            ++min;
        }
        return String.format("%d:%02d", min, sec);
    }

    public static String formatHours(double seconds) {
        int min = (int)Math.floor(seconds / 60);
        int sec = (int)(seconds - min*60 + 0.5);
        if (sec == 60) {
            sec = 0;
            ++min;
        }
        int hour = min / 60;
        min -= hour * 60;
        return String.format("%d:%02d:%02d", hour, min, sec);
    }

    public static String formatHundredths(double seconds) {
        int min = (int)Math.floor(seconds / 60);
        int sec = (int)(seconds - min*60);
        int hnd = (int)((seconds - min*60 - sec) * 100 + 0.5);
        if (hnd == 100) {
            hnd = 0;
            if (++sec == 60) {
                sec = 0;
                ++min;
            }
        }
        return String.format("%d:%02d.%02d", min, sec, hnd);
    }

    public static String formatHundredthsNoMinutes(double seconds) {
        int sec = (int)(seconds);
        int hnd = (int)((seconds - sec) * 100 + 0.5);
        if (hnd == 100) {
            hnd = 0;
            ++sec;
        }
        return String.format("%02d.%02d", sec, hnd);
    }

    public static String formatVariablePrecision(double seconds) {
//        if (seconds < 59.995) return formatHundredthsNoMinutes(seconds);
//        if (seconds < 3599.5) return formatSeconds(seconds);
//        if (seconds < 599.5) return formatHundredths(seconds);
//        if (seconds < 3599.5) return formatSeconds(seconds);
        if (seconds < 3599.5) return formatHundredths(seconds);
        return formatHours(seconds);
    }
}
