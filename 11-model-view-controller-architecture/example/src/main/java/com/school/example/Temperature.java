package com.school.example;

/**
 * M = MODEL: the data and the RULES. Plain Java: no request, no response, no HTML.
 * It could be used by a website, a phone app, or a test, without changing a line.
 */
public class Temperature {

    public static final double ABSOLUTE_ZERO = -273.15;

    private final double celsius;

    public Temperature(double celsius) {
        if (celsius < ABSOLUTE_ZERO) {
            throw new IllegalArgumentException("Nothing is colder than -273.15 °C.");
        }
        this.celsius = celsius;
    }

    public double getCelsius() {
        return celsius;
    }

    public double getFahrenheit() {
        return Math.round((celsius * 9 / 5 + 32) * 10) / 10.0;
    }

    public String getFeeling() {
        if (celsius < 15) return "cold 🥶";
        if (celsius < 30) return "warm 🙂";
        return "hot 🥵";
    }
}
