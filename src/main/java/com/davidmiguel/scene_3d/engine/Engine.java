package com.davidmiguel.scene_3d.engine;

import com.davidmiguel.scene_3d.meshes.Face;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.utils.MathUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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

    public Engine(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * This function is called to clear the back buffer and canvas.
     */
    public void clear() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // Clear backBuffer
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        backBuffer = gc.getCanvas().snapshot(params, null);
    }

    /**
     * Re-compute each vertex projection during each frame.
     */
    public void render(Camera camera, Mesh[] meshes) {
        Matrix4d viewMatrix = MathUtils.lookAtLH(camera.getPosition(), camera.getTarget(), MathUtils.UP);
        Matrix4d projectionMatrix = MathUtils.perspectiveFovLH(
                0.78, gc.getCanvas().getWidth() / gc.getCanvas().getHeight(), 0.01, 1.0);

        for (Mesh mesh : meshes) {
            // Apply rotation and then translation
            Matrix4d worldMatrix = MathUtils.rotationYawPitchRoll(
                    mesh.getRotation().y, mesh.getRotation().x, mesh.getRotation().z);
            worldMatrix.mul(MathUtils.translation(
                    mesh.getPosition().x, mesh.getPosition().y, mesh.getPosition().z));

            Matrix4d transformMatrix = new Matrix4d(worldMatrix);
            transformMatrix.mul(viewMatrix);
            transformMatrix.mul(projectionMatrix);

            // Draw faces
            for (Face face : mesh.getFaces()) {
                // Project the 3D coordinates into the 2D space
                Vector3d vertexA = mesh.getVertices()[face.getA()];
                Vector3d vertexB = mesh.getVertices()[face.getB()];
                Vector3d vertexC = mesh.getVertices()[face.getC()];
                Vector2d pixelA = this.project(vertexA, transformMatrix);
                Vector2d pixelB = this.project(vertexB, transformMatrix);
                Vector2d pixelC = this.project(vertexC, transformMatrix);
                // Draw triangle
                this.drawLine(pixelA, pixelB);
                this.drawLine(pixelB, pixelC);
                this.drawLine(pixelC, pixelA);
            }
        }
    }

    /**
     * Flush the back buffer into the canvas.
     */
    public void draw() {
        gc.drawImage(backBuffer, 0, 0);
    }

    /**
     * Project takes some 3D coordinates and transform them in
     * 2D coordinates using the transformation matrix.
     */
    private Vector2d project(Vector3d coord, Matrix4d transMat) {
        // Transforming the coordinates
        Vector3d point = MathUtils.transformCoordinates(coord, transMat);
        // Transform from coordinate system starting at center to another starting at top left
        double x = point.x * gc.getCanvas().getWidth() + gc.getCanvas().getWidth() / 2.0;
        double y = -point.y * gc.getCanvas().getHeight() + gc.getCanvas().getHeight() / 2.0;
        return new Vector2d(x, y);
    }

    /**
     * Draw line with Bresenham’s line algorithm.
     */
    private void drawLine(Vector2d p1, Vector2d p2) {
        int x0 = (int)p1.x;
        int y0 = (int)p1.y;
        int x1 = (int)p2.x;
        int y1 = (int)p2.y;

        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);
        double sx = (x0 < x1) ? 1 : -1;
        double sy = (y0 < y1) ? 1 : -1;
        double err = dx - dy;

        while (true) {
            drawPoint(new Vector2d(x0, y0));
            if ((x0 == x1) && (y0 == y1)) break;
            double e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

    /**
     * Calls putPixel but does the clipping operation before.
     */
    private void drawPoint(Vector2d point) {
        // Clipping what's visible on screen
        if (point.x >= 0 && point.y >= 0
                && point.x < gc.getCanvas().getWidth()
                && point.y < this.gc.getCanvas().getHeight()) {
            // Drawing point
            this.putPixel((int) point.x, (int) point.y, Color.WHITE);
        }
    }

    /**
     * Put a pixel on backBuffer at specific x,y coordinates.
     */
    private void putPixel(int x, int y, Color color) {
        PixelWriter pw = backBuffer.getPixelWriter();
        // Write new pixel
        pw.setColor(x, y, color);
    }
}
