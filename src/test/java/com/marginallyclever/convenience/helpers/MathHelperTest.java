package com.marginallyclever.convenience.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    // Test for lengthSquared method
    @Test
    public void testLengthSquared() {
        // Arrange
        double dx = 3.0;
        double dy = 4.0;

        // Act
        double result = MathHelper.lengthSquared(dx, dy);

        // Assert
        Assertions.assertEquals(25.0, result, 1e-9, "The length squared should be 25.0");
    }

    // Test for equals method
    @Test
    public void testEquals() {
        // Arrange
        Tuple2d a0 = new Point2d(1.0, 1.0);
        Tuple2d a1 = new Point2d(2.0, 2.0);
        Tuple2d b0 = new Point2d(1.0, 1.0);
        Tuple2d b1 = new Point2d(2.0, 2.0);
        double epsilon = 1e-9;

        // Act & Assert
        Assertions.assertTrue(MathHelper.equals(a0, a1, b0, b1, epsilon), "The segments should be equal");
        Assertions.assertTrue(MathHelper.equals(a0, a1, b1, b0, epsilon), "The segments should be equal when reversed");

        // Test for inequality
        Tuple2d c0 = new Point2d(1.0, 1.0);
        Tuple2d c1 = new Point2d(3.0, 3.0);
        Assertions.assertFalse(MathHelper.equals(a0, a1, c0, c1, epsilon), "The segments should not be equal");
    }

    // Test for lerp method
    @Test
    public void testLerp() {
        // Arrange
        double t = 0.5;
        double a = 1.0;
        double b = 3.0;

        // Act
        double result = MathHelper.lerp(t, a, b);

        // Assert
        Assertions.assertEquals(2.0, result, 1e-9, "The lerp result should be 2.0");
    }
}
