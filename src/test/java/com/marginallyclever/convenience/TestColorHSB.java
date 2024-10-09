package com.marginallyclever.convenience;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TestColorHSB {

    private static final float delta = 1e-6f; // Allowable margin of error
    private ColorHSB color1;
    private ColorHSB color2;

    // Méthode source pour les tests paramétrés
    private static Stream<Arguments> provideColorHSBValues() {
        return Stream.of(Arguments.of(0.2f, 0.3f, 0.4f, 0.1f, 0.2f, 0.3f), Arguments.of(0.5f, 0.6f, 0.7f, 0.2f, 0.3f, 0.4f), Arguments.of(0.8f, 0.9f, 1.0f, 0.3f, 0.4f, 0.5f));
    }

    private static Stream<Arguments> provideColorHSBValuesForMul() {
        return Stream.of(Arguments.of(0.2f, 0.3f, 0.4f, 2.0), Arguments.of(0.5f, 0.6f, 0.7f, 1.5), Arguments.of(0.8f, 0.9f, 1.0f, 0.5));
    }

    private static Stream<Arguments> provideColorHSBValuesForSet() {
        return Stream.of(Arguments.of(0.2f, 0.3f, 0.4f), Arguments.of(0.5f, 0.6f, 0.7f), Arguments.of(0.8f, 0.9f, 1.0f));
    }

    private static Stream<Arguments> provideColorHSBValuesForEquals() {
        return Stream.of(Arguments.of(0.2f, 0.3f, 0.4f, 0.2f, 0.3f, 0.4f, true), Arguments.of(0.5f, 0.6f, 0.7f, 0.5f, 0.6f, 0.7f, true), Arguments.of(0.8f, 0.9f, 1.0f, 0.3f, 0.4f, 0.5f, false));
    }

    private void assertColorsEqual(ColorHSB c, float hue, float saturation, float brightness) {
        Assertions.assertEquals(hue, c.hue);
        Assertions.assertEquals(saturation, c.saturation);
        Assertions.assertEquals(brightness, c.brightness);
    }

    @Test
    public void limitRange() {
        assertColorsEqual(new ColorHSB(), 0, 0, 0);
        assertColorsEqual(new ColorHSB(1, 0, 0), 1, 0, 0);
        assertColorsEqual(new ColorHSB(0, 1, 0), 0, 1, 0);
        assertColorsEqual(new ColorHSB(0, 0, 1), 0, 0, 1);
        assertColorsEqual(new ColorHSB(-1, 0, 0), 0, 0, 0);
        assertColorsEqual(new ColorHSB(0, -1, 0), 0, 0, 0);
        assertColorsEqual(new ColorHSB(0, 0, -1), 0, 0, 0);
        assertColorsEqual(new ColorHSB(2, 0, 0), 1, 0, 0);
        assertColorsEqual(new ColorHSB(0, 2, 0), 0, 1, 0);
        assertColorsEqual(new ColorHSB(0, 0, 2), 0, 0, 1);
    }

    @BeforeEach
    public void setUp() {
        color1 = new ColorHSB();
        color2 = new ColorHSB();
    }

    // Test paramétré pour la méthode set(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValuesForSet")
    public void testSet(float hue, float saturation, float brightness) {
        // Act
        color1.set(hue, saturation, brightness);

        // Assert
        assertColorsEqual(color1, clamp(hue), clamp(saturation), clamp(brightness));
    }

    // Test paramétré pour la méthode sub(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValues")
    public void testSub(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2) {
        // Arrange
        color1.set(hue1, saturation1, brightness1);
        color2.set(hue2, saturation2, brightness2);

        // Act
        ColorHSB result = color1.sub(color2);

        // Assert
        assertColorsEqual(result, clamp(hue1 - hue2), clamp(saturation1 - saturation2), clamp(brightness1 - brightness2));
    }

    // Test paramétré pour la méthode add(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValues")
    public void testAdd(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2) {
        // Arrange
        color1.set(hue1, saturation1, brightness1);
        color2.set(hue2, saturation2, brightness2);

        // Act
        ColorHSB result = color1.add(color2);

        // Assert
        assertColorsEqual(result, clamp(hue1 + hue2), clamp(saturation1 + saturation2), clamp(brightness1 + brightness2));
    }

    // Test paramétré pour la méthode mul(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValuesForMul")
    public void testMul(float hue, float saturation, float brightness, double factor) {
        // Arrange
        color1.set(hue, saturation, brightness);

        // Act
        ColorHSB result = color1.mul(factor);

        // Assert
        assertColorsEqual(result, clamp((float) (hue * factor)), clamp((float) (saturation * factor)), clamp((float) (brightness * factor)));
    }

    // Test paramétré pour la méthode diffSquared(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValues")
    public void testDiffSquared(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2) {
        // Arrange
        color1.set(hue1, saturation1, brightness1);
        color2.set(hue2, saturation2, brightness2);

        // Act
        float result = color1.diffSquared(color2);

        // Assert
        float expected = (hue2 - hue1) * (hue2 - hue1) + (saturation2 - saturation1) * (saturation2 - saturation1) + (brightness2 - brightness1) * (brightness2 - brightness1);
        Assertions.assertEquals(expected, result, delta);
    }

    // Test paramétré pour la méthode diff(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValues")
    public void testDiff(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2) {
        // Arrange
        color1.set(hue1, saturation1, brightness1);
        color2.set(hue2, saturation2, brightness2);

        // Act
        float result = color1.diff(color2);

        // Assert
        float expected = (float) Math.sqrt((hue2 - hue1) * (hue2 - hue1) + (saturation2 - saturation1) * (saturation2 - saturation1) + (brightness2 - brightness1) * (brightness2 - brightness1));
        Assertions.assertEquals(expected, result, delta);
    }

    // Test paramétré pour la méthode equals(...)
    @ParameterizedTest
    @MethodSource("provideColorHSBValuesForEquals")
    public void testEquals(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2, boolean expected) {
        // Arrange
        color1.set(hue1, saturation1, brightness1);
        color2.set(hue2, saturation2, brightness2);

        // Act & Assert
        Assertions.assertEquals(expected, color1.equals(color2));
    }

    // Méthode pour clamp value entre 0.0 et 1.0
    private float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

}
