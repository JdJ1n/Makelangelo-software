package com.marginallyclever.convenience.helpers;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class OSHelperTest {

    private static final Faker faker = new Faker();

    // Method to provide random OS names for parameterized tests
    private static Stream<String> provideRandomWindowsNames() {
        return Stream.of(
                "Windows " + faker.number().numberBetween(1, 10),
                "Windows " + faker.number().numberBetween(11, 20),
                "Windows " + faker.number().numberBetween(21, 30)
        );
    }

    private static Stream<String> provideRandomOSXNames() {
        return Stream.of(
                "Mac OS X " + faker.number().numberBetween(1, 10),
                "macOS " + faker.number().numberBetween(11, 20),
                "OSX " + faker.number().numberBetween(21, 30)
        );
    }

    // Parameterized test for isWindows method
    @ParameterizedTest
    @MethodSource("provideRandomWindowsNames")
    public void testIsWindowsParameterized(String osName) {
        // Arrange
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", osName);

        try {
            // Act
            boolean result = OSHelper.isWindows();

            // Assert
            Assertions.assertTrue(result, "The OS should be detected as Windows");
        } finally {
            // Restore the original system property
            System.setProperty("os.name", originalOsName);
        }
    }

    // Parameterized test for isOSX method
    @ParameterizedTest
    @MethodSource("provideRandomOSXNames")
    public void testIsOSXParameterized(String osName) {
        // Arrange
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", osName);

        try {
            // Act
            boolean result = OSHelper.isOSX();

            // Assert
            Assertions.assertTrue(result, "The OS should be detected as OSX");
        } finally {
            // Restore the original system property
            System.setProperty("os.name", originalOsName);
        }
    }
}
