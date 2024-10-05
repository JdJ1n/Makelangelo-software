package com.marginallyclever.convenience.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.javafaker.Faker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;

public class MathHelperTest {
    @Test
    public void testBetween() {
        Point2d a = new Point2d();
        Point2d b = new Point2d();
        Point2d c;
        double epsilon = 1e-9;

        for (int i = 0; i < 50; ++i) {
            a.set(Math.random() * 500 - 250, Math.random() * 500 - 250);
            b.set(Math.random() * 500 - 250, Math.random() * 500 - 250);
            c = MathHelper.lerp(a, b, Math.random());
            assert (MathHelper.between(a, b, c, epsilon));
        }
    }

    @Test
    public void testNotBetween() {
        Point2d a = new Point2d();
        Point2d b = new Point2d();
        Point2d c;
        double epsilon = 1e-9;

        for(int i=0;i<50;++i) {
            a.set(Math.random()*500-250, Math.random()*500-250);
            b.set(Math.random()*500-250, Math.random()*500-250);
            c = MathHelper.lerp(a,b,1.0+epsilon+Math.random());
            Assertions.assertFalse(MathHelper.between(a, b, c, epsilon));
        }

        for(int i=0;i<50;++i) {
            a.set(Math.random()*500-250, Math.random()*500-250);
            b.set(Math.random()*500-250, Math.random()*500-250);
            c = MathHelper.lerp(a,b,-epsilon-Math.random());
            Assertions.assertFalse(MathHelper.between(a, b, c, epsilon));
        }
    }

    @Test
    public void testOffLine() {
        Point2d a = new Point2d();
        Point2d b = new Point2d();
        Point2d ortho = new Point2d();
        Point2d c;
        double epsilon = 1e-9;

        for(int i=0;i<50;++i) {
            a.set(Math.random()*500-250, Math.random()*500-250);
            b.set(Math.random()*500-250, Math.random()*500-250);
            c = MathHelper.lerp(a,b,Math.random());
            ortho.set(b);
            ortho.sub(a);
            c.x+=ortho.y;
            c.y+=ortho.x;
            Assertions.assertFalse(MathHelper.between(a, b, c, epsilon));
        }

        for(int i=0;i<50;++i) {
            a.set(Math.random()*500-250, Math.random()*500-250);
            b.set(Math.random()*500-250, Math.random()*500-250);
            c = MathHelper.lerp(a,b,1.0+epsilon+Math.random());
            ortho.set(b);
            ortho.sub(a);
            c.x+=ortho.y;
            c.y+=ortho.x;
            Assertions.assertFalse(MathHelper.between(a, b, c, epsilon));
        }

        for(int i=0;i<50;++i) {
            a.set(Math.random()*500-250, Math.random()*500-250);
            b.set(Math.random()*500-250, Math.random()*500-250);
            c = MathHelper.lerp(a,b,-epsilon-Math.random());
            ortho.set(b);
            ortho.sub(a);
            c.x+=ortho.y;
            c.y+=ortho.x;
            Assertions.assertFalse(MathHelper.between(a, b, c, epsilon));
        }
    }

    private final Faker faker = new Faker();

    // Parameterized test for lengthSquared method
    @ParameterizedTest
    @CsvSource({
            "3.0, 4.0, 25.0",
            "5.0, 12.0, 169.0",
            "8.0, 15.0, 289.0"
    })
    public void testLengthSquared(double dx, double dy, double expected) {
        // Act
        double result = MathHelper.lengthSquared(dx, dy);

        // Assert
        Assertions.assertEquals(expected, result, 1e-9, "The length squared should be " + expected);
    }

    // Test for equals method using javafaker
    @Test
    public void testEquals() {
        // Arrange
        Tuple2d a0 = new Point2d(faker.number().randomDouble(2, -100, 100), faker.number().randomDouble(2, -100, 100));
        Tuple2d a1 = new Point2d(faker.number().randomDouble(2, -100, 100), faker.number().randomDouble(2, -100, 100));
        Tuple2d b0 = new Point2d(a0.x, a0.y);
        Tuple2d b1 = new Point2d(a1.x, a1.y);
        double epsilon = 1e-9;

        // Act & Assert
        Assertions.assertTrue(MathHelper.equals(a0, a1, b0, b1, epsilon), "The segments should be equal");
        Assertions.assertTrue(MathHelper.equals(a0, a1, b1, b0, epsilon), "The segments should be equal when reversed");

        // Test for inequality
        Tuple2d c0 = new Point2d(faker.number().randomDouble(2, -100, 100), faker.number().randomDouble(2, -100, 100));
        Tuple2d c1 = new Point2d(faker.number().randomDouble(2, -100, 100), faker.number().randomDouble(2, -100, 100));
        Assertions.assertFalse(MathHelper.equals(a0, a1, c0, c1, epsilon), "The segments should not be equal");
    }

    // Parameterized test for lerp method
    @ParameterizedTest
    @CsvSource({
            "0.5, 1.0, 3.0, 2.0",
            "0.25, 2.0, 6.0, 3.0",
            "0.75, 0.0, 4.0, 3.0"
    })
    public void testLerp(double t, double a, double b, double expected) {
        // Act
        double result = MathHelper.lerp(t, a, b);

        // Assert
        Assertions.assertEquals(expected, result, 1e-9, "The lerp result should be " + expected);
    }
}
