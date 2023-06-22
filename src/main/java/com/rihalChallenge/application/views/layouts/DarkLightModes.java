package com.rihalChallenge.application.views.layouts;

public class DarkLightModes {
    private static boolean isDarkMode = false;

    public static void toggleDarkMode() {
        isDarkMode = !isDarkMode;
    }

    public static String getTheme() {
        return isDarkMode ? "dark" : "light";
    }

    public static boolean isDarkMode(){
        return isDarkMode;
    }
}
