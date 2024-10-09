package com.marginallyclever.convenience.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StringHelperTest {

    // Test paramétré pour le format de Float
    @ParameterizedTest
    @CsvSource({"1.23456, 1.235", "0.12345, 0.123", "-1.23456, -1.235"}) // Arrange
    public void testFormatFloat(float input, String expected) {
        // Act
        String result = StringHelper.formatFloat(input);

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // Test paramétré pour le format de droite
    @ParameterizedTest
    @CsvSource({"'hello', 10, 'hello     '", "'world', 8, 'world   '", "'java', 6, 'java  '"}) // Arrange
    public void testPadRight(String input, int length, String expected) {
        // Act
        String result = StringHelper.padRight(input, length);

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // Test paramétré pour la transition de temp
    @ParameterizedTest
    @CsvSource({"3661, '01:01:01'", "7322, '02:02:02'", "86400, '00:00:24'", "86424, '00:00:24'"}) // Arrange
    public void testGetElapsedTime(int seconds, String expected) {
        // Act
        String result = StringHelper.getElapsedTime(seconds);

        // Assert
        Assertions.assertEquals(expected, result);
    }
}
