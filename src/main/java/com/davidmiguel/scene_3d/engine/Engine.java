package com.davidmiguel.scene_3d.engine;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageWriter;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * 3D engine.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Engine {

    private WritableImage backBuffer;
    private GraphicsContext gc;
    private Camera camera;

    public Engine(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * This function is called to clear the back buffer and canvas.
     */
    public void clear() {
        // Clear canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // Clear backBuffer
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        backBuffer = gc.getCanvas().snapshot(params, null);
    }

    /**
     * Flush the back buffer into the canvas.
     */
    public void draw() {
        gc.drawImage(backBuffer, 0, 0);
    }

    /**
     * Put a pixel on backBuffer at specific x,y coordinates.
     */
    public void putPixel(int x, int y, Color color) {
        PixelWriter pw = backBuffer.getPixelWriter();
        // Write new pixel
        pw.setColor(x, y, color);
    }

    /**
     * Project takes some 3D coordinates and transform them in
     * 2D coordinates using the transformation matrix.
     */
    public Vector2d project(Vector3d coord, Matrix4d transMat) {
        // Transforming the coordinates
        Vector3d point = transformCoordinates(coord, transMat);
        // Transform from coordinate system starting at center to another starting at top left
        double x = point.x * gc.getCanvas().getWidth() + gc.getCanvas().getWidth() / 2.0;
        double y = -point.y * gc.getCanvas().getHeight() + gc.getCanvas().getHeight() / 2.0;
        return new Vector2d(x, y);
    }

    /**
     * Calls putPixel but does the clipping operation before.
     */
    public void drawPoint(Vector2d point) {
        // Clipping what's visible on screen
        if (point.x >= 0 && point.y >= 0
                && point.x < gc.getCanvas().getWidth()
                && point.y < this.gc.getCanvas().getHeight()) {
            // Drawing point
            this.putPixel((int) point.x, (int) point.y, Color.BLACK);
        }
    }

    private Vector3d transformCoordinates(Vector3d coord, Matrix4d transMat) {
        double x = (coord.x * transMat.m00) + (coord.y * transMat.m01) + (coord.z * transMat.m02) + transMat.m03;
        double y = (coord.x * transMat.m10) + (coord.y * transMat.m11) + (coord.z * transMat.m12) + transMat.m13;
        double z = (coord.x * transMat.m20) + (coord.y * transMat.m21) + (coord.z * transMat.m22) + transMat.m23;
        double w = (coord.x * transMat.m30) + (coord.y * transMat.m31) + (coord.z * transMat.m32) + transMat.m33;
        return new Vector3d(x / w, y / w, z / w);
    }
}
