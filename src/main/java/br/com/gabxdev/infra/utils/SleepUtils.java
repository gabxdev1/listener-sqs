package br.com.gabxdev.infra.utils;

public class SleepUtils {

    public static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
