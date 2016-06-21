package com.davidmiguel.scene_3d.utils;

import com.davidmiguel.scene_3d.meshes.Vertex;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.vecmath.Vector3d;

/**
 * Methods to draw.
 *
 * @author davidmigloz
 * @since 20/06/2016
 */
@SuppressWarnings("WeakerAccess")
public class DrawUtils {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 550;

    /**
     * Calls putPixel but does the clipping operation before.
     */
    public static void drawPoint(WritableImage img, Vector3d point, Color color) {
        // Clipping what's visible on screen
        if (point.x >= 0 && point.y >= 0
                && point.x < WIDTH
                && point.y < HEIGHT) {
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
            drawPoint(img, new Vector3d(x0, y0, 0), color);
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

    public static void drawFilledTriangle(WritableImage img, Vertex v1, Vertex v2, Vertex v3, Color color) {
        drawLine(img, v1.getCoordinates(), v2.getCoordinates(), color);
        drawLine(img, v2.getCoordinates(), v3.getCoordinates(), color);
        drawLine(img, v3.getCoordinates(), v1.getCoordinates(), color);
    }

    @SuppressWarnings("Duplicates")
    public static void drawFilledTriangle(WritableImage img, double[][] depthBuffer,
                                          Vertex v1, Vertex v2, Vertex v3, Color color) {
        // Sorting the points in order to always have this order on screen p1, p2 & p3
        // with p1 always up (thus having the Y the lowest possible to be near the top screen)
        // then p2 between p1 & p3
        if (v1.getCoordinates().y > v2.getCoordinates().y) {
            Vertex temp = v2;
            v2 = v1;
            v1 = temp;
        }

        if (v2.getCoordinates().y > v3.getCoordinates().y) {
            Vertex temp = v2;
            v2 = v3;
            v3 = temp;
        }

        if (v1.getCoordinates().y > v2.getCoordinates().y) {
            Vertex temp = v2;
            v2 = v1;
            v1 = temp;
        }

        Vector3d p1 = v1.getCoordinates();
        Vector3d p2 = v2.getCoordinates();
        Vector3d p3 = v3.getCoordinates();

        // Normal face's vector is the average normal between each vertex's normal
        Vector3d vnFace = new Vector3d();
        vnFace.add(v1.getNormal(), v2.getNormal());
        vnFace.add(v3.getNormal());
        vnFace.x /= 3.0;
        vnFace.y /= 3.0;
        vnFace.z /= 3.0;
        // Computing also the center point of the face
        Vector3d centerPoint = new Vector3d();
        centerPoint.add(v1.getWorldCoordinates(), v2.getWorldCoordinates());
        centerPoint.add(v3.getWorldCoordinates());
        centerPoint.x /= 3.0;
        centerPoint.y /= 3.0;
        centerPoint.z /= 3.0;

        // Light position
        Vector3d lightPos = new Vector3d(0, 10, 10);
        // Computing the cos of the angle between the light vector and the normal vector
        // it will return a value between 0 and 1 that will be used as the intensity of the color
        double ndotl = MathUtils.computeNDotL(centerPoint, vnFace, lightPos);

        ScanLineData data = new ScanLineData(ndotl);

        // Lines' directions
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
                data.setCurrentY(y);
                if (y < p2.y) {
                    processScanLine(img, depthBuffer, data, v1, v3, v1, v2, color);
                } else {
                    processScanLine(img, depthBuffer, data, v1, v3, v2, v3, color);
                }
            }
        } else {
            // Second case where triangles are like that: P1-P2(left)-P3 (from top to bottom)
            for (int y = (int) p1.y; y <= (int) p3.y; y++) {
                data.setCurrentY(y);
                if (y < p2.y) {
                    processScanLine(img, depthBuffer, data, v1, v2, v1, v3, color);
                } else {
                    processScanLine(img, depthBuffer, data, v2, v3, v1, v3, color);
                }
            }
        }
    }

    /**
     * Drawing line between 2 points from left to right.
     * papb -> pcpd
     * pa, pb, pc, pd must then be sorted before.
     */
    private static void processScanLine(WritableImage img, double[][] depthBuffer, ScanLineData data,
                                        Vertex va, Vertex vb, Vertex vc, Vertex vd, Color color) {
        Vector3d pa = va.getCoordinates();
        Vector3d pb = vb.getCoordinates();
        Vector3d pc = vc.getCoordinates();
        Vector3d pd = vd.getCoordinates();
        // Thanks to current Y, we can compute the gradient to compute others values like
        // the starting X (sx) and ending X (ex) to draw between
        // if pa.Y == pb.Y or pc.Y == pd.Y, gradient is forced to 1
        double gradient1 = pa.y != pb.y ? ((data.getCurrentY() - pa.y) / (pb.y - pa.y)) : 1;
        double gradient2 = pc.y != pd.y ? ((data.getCurrentY() - pc.y) / (pd.y - pc.y)) : 1;

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
            if (depthBuffer[x][data.getCurrentY()] >= z) {
                depthBuffer[x][data.getCurrentY()] = z;
                drawPoint(img, new Vector3d(x, data.getCurrentY(), z),
                        new Color(color.getRed() * data.getNdotla(), color.getGreen() * data.getNdotla(),
                                color.getBlue() * data.getNdotla(), 1.0));
            }
        }
    }

    /**
     * Put a pixel on backBuffer at specific x,y coordinates.
     */
    private static synchronized void putPixel(WritableImage img, int x, int y, Color color) {
        PixelWriter pw = img.getPixelWriter();
        pw.setColor(x, y, color);
    }
}
