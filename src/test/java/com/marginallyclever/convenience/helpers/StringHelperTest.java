package com.marginallyclever.convenience.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StringHelperTest {

    // Parameterized test for formatFloat method
    @ParameterizedTest
    @CsvSource({
            "1.23456, 1.235",
            "0.12345, 0.123",
            "-1.23456, -1.235"
    })
    public void testFormatFloat(float input, String expected) {
        // Act
        String result = StringHelper.formatFloat(input);

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // Parameterized test for padRight method
    @ParameterizedTest
    @CsvSource({
            "'hello', 10, 'hello     '",
            "'world', 8, 'world   '",
            "'java', 6, 'java  '"
    })
    public void testPadRight(String input, int length, String expected) {
        // Act
        String result = StringHelper.padRight(input, length);

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // Parameterized test for getElapsedTime method
    @ParameterizedTest
    @CsvSource({
            "3661, '01:01:01'",
            "7322, '02:02:02'",
            "86400, '00:00:24'",
            "86424, '00:00:24'"
    })
    public void testGetElapsedTime(int seconds, String expected) {
        // Act
        String result = StringHelper.getElapsedTime(seconds);

        // Assert
        Assertions.assertEquals(expected, result);
    }
}
