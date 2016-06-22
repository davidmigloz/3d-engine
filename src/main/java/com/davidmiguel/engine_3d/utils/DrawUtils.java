package com.davidmiguel.engine_3d.utils;

import com.davidmiguel.engine_3d.meshes.Texture;
import com.davidmiguel.engine_3d.meshes.Vertex;
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

    /**
     * Draw a triangle drawing its edges.
     */
    public static void drawTriangle(WritableImage img, Vertex v1, Vertex v2, Vertex v3, Color color) {
        drawLine(img, v1.getCoordinates(), v2.getCoordinates(), color);
        drawLine(img, v2.getCoordinates(), v3.getCoordinates(), color);
        drawLine(img, v3.getCoordinates(), v1.getCoordinates(), color);
    }

    /**
     * Draw a filled triangle with texture using scan-line algorithm.
     */
    @SuppressWarnings("Duplicates")
    public static void drawFilledTriangle(WritableImage img, double[][] depthBuffer,
                                          Vertex v1, Vertex v2, Vertex v3, Color color, Texture texture) {
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

        // Light position
        Vector3d lightPos = new Vector3d(0, 10, 10);
        // Computing the cos of the angle between the light vector and the normal vector
        // it will return a value between 0 and 1 that will be used as the intensity of the color
        double nl1 = MathUtils.computeNDotL(v1.getWorldCoordinates(), v1.getNormal(), lightPos);
        double nl2 = MathUtils.computeNDotL(v2.getWorldCoordinates(), v2.getNormal(), lightPos);
        double nl3 = MathUtils.computeNDotL(v3.getWorldCoordinates(), v3.getNormal(), lightPos);

        ScanLineData data = new ScanLineData();

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
                    data.setNdotla(nl1);
                    data.setNdotlb(nl3);
                    data.setNdotlc(nl1);
                    data.setNdotld(nl2);
                    if(texture != null) {
                        data.setUa(v1.getTextureCoordinates().x);
                        data.setUb(v3.getTextureCoordinates().x);
                        data.setUc(v1.getTextureCoordinates().x);
                        data.setUd(v2.getTextureCoordinates().x);

                        data.setVa(v1.getTextureCoordinates().y);
                        data.setVb(v3.getTextureCoordinates().y);
                        data.setVc(v1.getTextureCoordinates().y);
                        data.setVd(v2.getTextureCoordinates().y);
                    }
                    processScanLine(img, depthBuffer, data, v1, v3, v1, v2, color, texture);
                } else {
                    data.setNdotla(nl1);
                    data.setNdotlb(nl3);
                    data.setNdotlc(nl2);
                    data.setNdotld(nl3);
                    if(texture != null) {
                        data.setUa(v1.getTextureCoordinates().x);
                        data.setUb(v3.getTextureCoordinates().x);
                        data.setUc(v2.getTextureCoordinates().x);
                        data.setUd(v3.getTextureCoordinates().x);

                        data.setVa(v1.getTextureCoordinates().y);
                        data.setVb(v3.getTextureCoordinates().y);
                        data.setVc(v2.getTextureCoordinates().y);
                        data.setVd(v3.getTextureCoordinates().y);
                    }
                    processScanLine(img, depthBuffer, data, v1, v3, v2, v3, color, texture);
                }
            }
        } else {
            // Second case where triangles are like that: P1-P2(left)-P3 (from top to bottom)
            for (int y = (int) p1.y; y <= (int) p3.y; y++) {
                data.setCurrentY(y);
                if (y < p2.y) {
                    data.setNdotla(nl1);
                    data.setNdotlb(nl2);
                    data.setNdotlc(nl1);
                    data.setNdotld(nl3);
                    if(texture != null) {
                        data.setUa(v1.getTextureCoordinates().x);
                        data.setUb(v2.getTextureCoordinates().x);
                        data.setUc(v1.getTextureCoordinates().x);
                        data.setUd(v3.getTextureCoordinates().x);

                        data.setVa(v1.getTextureCoordinates().y);
                        data.setVb(v2.getTextureCoordinates().y);
                        data.setVc(v1.getTextureCoordinates().y);
                        data.setVd(v3.getTextureCoordinates().y);
                    }
                    processScanLine(img, depthBuffer, data, v1, v2, v1, v3, color, texture);
                } else {
                    data.setNdotla(nl2);
                    data.setNdotlb(nl3);
                    data.setNdotlc(nl1);
                    data.setNdotld(nl3);
                    if(texture != null) {
                        data.setUa(v2.getTextureCoordinates().x);
                        data.setUb(v3.getTextureCoordinates().x);
                        data.setUc(v1.getTextureCoordinates().x);
                        data.setUd(v3.getTextureCoordinates().x);

                        data.setVa(v2.getTextureCoordinates().y);
                        data.setVb(v3.getTextureCoordinates().y);
                        data.setVc(v1.getTextureCoordinates().y);
                        data.setVd(v3.getTextureCoordinates().y);
                    }
                    processScanLine(img, depthBuffer, data, v2, v3, v1, v3, color, texture);
                }
            }
        }
    }

    /**
     * Draw a filled triangle without texture using scan-line algorithm.
     */
    public static void drawFilledTriangle(WritableImage img, double[][] depthBuffer,
                                          Vertex v1, Vertex v2, Vertex v3, Color color) {
        drawFilledTriangle(img, depthBuffer, v1, v2, v3, color, null);
    }

    /**
     * Drawing line between 2 points from left to right.
     * papb -> pcpd
     * pa, pb, pc, pd must then be sorted before.
     */
    private static void processScanLine(WritableImage img, double[][] depthBuffer, ScanLineData data,
                                        Vertex va, Vertex vb, Vertex vc, Vertex vd, Color color, Texture texture) {
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
        // Starting and ending of color gradient
        double snl = MathUtils.interpolate(data.getNdotla(), data.getNdotlb(), gradient1);
        double enl = MathUtils.interpolate(data.getNdotlc(), data.getNdotld(), gradient2);
        // Interpolating texture coordinates on Y
        double su = 0, eu = 0, sv = 0, ev = 0;
        if (texture != null) {
            su = MathUtils.interpolate(data.getUa(), data.getUb(), gradient1);
            eu = MathUtils.interpolate(data.getUc(), data.getUd(), gradient2);
            sv = MathUtils.interpolate(data.getVa(), data.getVb(), gradient1);
            ev = MathUtils.interpolate(data.getVc(), data.getVd(), gradient2);
        }
        // Drawing a line from left (sx) to right (ex)
        for (int x = sx; x < ex; x++) {
            double gradient = (x - sx) / (double) (ex - sx);
            double z = MathUtils.interpolate(z1, z2, gradient);
            // Color according to light
            double ndotl = MathUtils.interpolate(snl, enl, gradient);
            double r = color.getRed() * ndotl;
            double g = color.getGreen() * ndotl;
            double b = color.getBlue() * ndotl;
            // Texture
            double u, v;
            if (texture != null) {
                u = MathUtils.interpolate(su, eu, gradient);
                v = MathUtils.interpolate(sv, ev, gradient);
                Color textureColor = texture.map(u, v);
                r *= textureColor.getRed();
                g *= textureColor.getGreen();
                b *= textureColor.getBlue();
            }
            // Draw point only if it is visible (Z-Buffering)
            if (depthBuffer[x][data.getCurrentY()] >= z) {
                depthBuffer[x][data.getCurrentY()] = z;
                drawPoint(img, new Vector3d(x, data.getCurrentY(), z), new Color(r, g, b, 1.0));
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
