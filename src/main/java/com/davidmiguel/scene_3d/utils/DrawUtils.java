package com.davidmiguel.scene_3d.utils;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Methods to draw.
 *
 * @author davidmigloz
 * @since 20/06/2016
 */
@SuppressWarnings("WeakerAccess")
public class DrawUtils {

    /**
     * Calls putPixel but does the clipping operation before.
     */
    public static void drawPoint(WritableImage img, Vector2d point, Color color) {
        // Clipping what's visible on screen
        if (point.x >= 0 && point.y >= 0
                && point.x < img.getWidth()
                && point.y < img.getHeight()) {
            // Drawing point
            putPixel(img, (int) point.x, (int) point.y, color);
        }
    }

    /**
     * Draw line with Bresenham’s line algorithm.
     */
    public static void drawLine(WritableImage img, int x0, int y0, int x1, int y1, Color color) {
        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);
        double sx = (x0 < x1) ? 1 : -1;
        double sy = (y0 < y1) ? 1 : -1;
        double err = dx - dy;

        while (true) {
            drawPoint(img, new Vector2d(x0, y0), color);
            if ((x0 == x1) && (y0 == y1)) break;
            double e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    /**
     * Draw line with Bresenham’s line algorithm.
     */
    public static void drawLine(WritableImage img, Vector3d p1, Vector3d p2, Color color) {
        drawLine(img, (int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y, color);
    }

    public static void drawFilledTriangle(WritableImage img, Vector3d p1, Vector3d p2, Vector3d p3, Color color) {
        drawLine(img, p1, p2, color);
        drawLine(img, p2, p3, color);
        drawLine(img, p3, p1, color);
    }

    public static void drawFilledTriangle(WritableImage img,
            Vector3d p1, Vector3d p2, Vector3d p3, double[][] depthBuffer, Color color) {
        // Sorting the points in order to always have this order on screen p1, p2 & p3
        // with p1 always up (thus having the Y the lowest possible to be near the top screen)
        // then p2 between p1 & p3
        if (p1.y > p2.y) {
            Vector3d temp = p2;
            p2 = p1;
            p1 = temp;
        }

        if (p2.y > p3.y) {
            Vector3d temp = p2;
            p2 = p3;
            p3 = temp;
        }

        if (p1.y > p2.y) {
            Vector3d temp = p2;
            p2 = p1;
            p1 = temp;
        }

        // Inverse slopes
        double dP1P2, dP1P3;

        // http://en.wikipedia.org/wiki/Slope
        // Computing inverse slopes
        if (p2.y - p1.y > 0) {
            dP1P2 = ((p2.x - p1.x) / (p2.y - p1.y));
        } else {
            dP1P2 = 0;
        }

        if (p3.y - p1.y > 0) {
            dP1P3 = ((p3.x - p1.x) / (p3.y - p1.y));
        } else {
            dP1P3 = 0;
        }

        // First case where triangles are like that: P1-P2(right)-P3 (from top to bottom)
        if (dP1P2 > dP1P3) {
            for (int y = (int) p1.y; y <= (int) p3.y; y++) {
                if (y < p2.y) {
                    processScanLine(img, y, p1, p3, p1, p2, depthBuffer, color);
                } else {
                    processScanLine(img, y, p1, p3, p2, p3, depthBuffer, color);
                }
            }
        } else {
            // Second case where triangles are like that: P1-P2(left)-P3 (from top to bottom)
            for (int y = (int) p1.y; y <= (int) p3.y; y++) {
                if (y < p2.y) {
                    processScanLine(img, y, p1, p2, p1, p3, depthBuffer, color);
                } else {
                    processScanLine(img, y, p2, p3, p1, p3, depthBuffer, color);
                }
            }
        }
    }

    /**
     * Drawing line between 2 points from left to right.
     * papb -> pcpd
     * pa, pb, pc, pd must then be sorted before.
     */
    private static void processScanLine(WritableImage img, int y,
            Vector3d pa, Vector3d pb, Vector3d pc, Vector3d pd, double[][] depthBuffer, Color color) {
        // Thanks to current Y, we can compute the gradient to compute others values like
        // the starting X (sx) and ending X (ex) to draw between
        // if pa.Y == pb.Y or pc.Y == pd.Y, gradient is forced to 1
        double gradient1 = pa.y != pb.y ? ((y - pa.y) / (pb.y - pa.y)) : 1;
        double gradient2 = pc.y != pd.y ? ((y - pc.y) / (pd.y - pc.y)) : 1;

        // Starting X & ending X
        int sx = (int) MathUtils.interpolate(pa.x, pb.x, gradient1);
        int ex = (int) MathUtils.interpolate(pc.x, pd.x, gradient2);
        // Starting Z & ending Z
        double z1 = MathUtils.interpolate(pa.z, pb.z, gradient1);
        double z2 = MathUtils.interpolate(pc.z, pd.z, gradient2);

        // Drawing a line from left (sx) to right (ex)
        for (int x = sx; x < ex; x++) {
            double gradient = (x - sx) / (double) (ex - sx);
            double z = MathUtils.interpolate(z1, z2, gradient);
            // Draw point only if it is visible (Z-Buffering)
            if(depthBuffer[x][y] >= z) {
                depthBuffer[x][y] = z;
                drawPoint(img, new Vector2d(x, y), color);
            }
        }
    }

    /**
     * Put a pixel on backBuffer at specific x,y coordinates.
     */
    private static void putPixel(WritableImage img, int x, int y, Color color) {
        PixelWriter pw = img.getPixelWriter();
        // Write new pixel
        int r = (int) (color.getRed() * (255));
        int g = (int) (color.getGreen() * (255));
        int b = (int) (color.getBlue() * (255));
        pw.setColor(x, y, Color.rgb(r, g, b));
    }
}
