package com.davidmiguel.scene_3d.engine;

import com.davidmiguel.scene_3d.meshes.Face;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.utils.DrawUtils;
import com.davidmiguel.scene_3d.utils.MathUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.Arrays;

/**
 * 3D engine.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Engine {

    public enum RenderMode {
        WIREFRAME, RASTERIZATION
    }

    /**
     * Image where we perform all transformations.
     */
    private WritableImage backBuffer;
    /**
     * Canvas where we flush the backBuffer with a fixed frequency.
     */
    private GraphicsContext gc;
    /**
     * Use for Z-Buffering.
     */
    private double[][] depthBuffer;

    private double width;
    private double height;

    public Engine(GraphicsContext gc) {
        this.gc = gc;
        width = gc.getCanvas().getWidth();
        height = gc.getCanvas().getHeight();
        this.depthBuffer = new double[(int) width][(int) height];
    }

    /**
     * Flush the back buffer into the canvas.
     */
    public void draw(Camera camera, Mesh[] meshes, RenderMode mode) {
        if (meshes == null || meshes.length == 0 || camera == null) {
            return;
        }
        // Clear the screen and all associated pixels with white ones
        this.clear();
        // Render them into the back buffer by doing the required matrix operations
        this.render(camera, meshes, mode);
        // Display them on screen by flushing the back buffer data into the front buffer
        gc.drawImage(backBuffer, 0, 0);
    }

    /**
     * This function is called to clear the back buffer and canvas.
     */
    private void clear() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
        // Clear backBuffer
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        backBuffer = gc.getCanvas().snapshot(params, null);
        // Clear depthBuffer
        for (double[] row: depthBuffer) {
            Arrays.fill(row, Double.MAX_VALUE);
        }
    }


    /**
     * Re-compute each vertex projection during each frame.
     */
    private void render(Camera camera, Mesh[] meshes, RenderMode mode) {
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
            int indexFaces = 0;
            for (Face face : mesh.getFaces()) {
                // Project the 3D coordinates into the 2D space
                Vector3d vertexA = mesh.getVertices()[face.getA()];
                Vector3d vertexB = mesh.getVertices()[face.getB()];
                Vector3d vertexC = mesh.getVertices()[face.getC()];
                Vector3d pixelA = this.project(vertexA, transformMatrix);
                Vector3d pixelB = this.project(vertexB, transformMatrix);
                Vector3d pixelC = this.project(vertexC, transformMatrix);
                // Draw
                switch (mode) {
                    case WIREFRAME:
                        DrawUtils.drawFilledTriangle(backBuffer, pixelA, pixelB, pixelC, Color.WHITE);
                        break;
                    case RASTERIZATION:
                        double color = (0.25
                                + ((indexFaces % mesh.getFaces().length)
                                / (double) mesh.getFaces().length)
                                * 0.75);
                        DrawUtils.drawFilledTriangle(backBuffer, pixelA, pixelB, pixelC,
                                depthBuffer, new Color(color, color, color, 1.0));
                        indexFaces++;
                        break;
                }
            }
        }
    }


    /**
     * Project takes some 3D coordinates and transform them in
     * 2D coordinates using the transformation matrix.
     */
    private Vector3d project(Vector3d coord, Matrix4d transMat) {
        // Transforming the coordinates
        Vector3d point = MathUtils.transformCoordinates(coord, transMat);
        // Transform from coordinate system starting at center to another starting at top left
        double x = point.x * width + width / 2.0;
        double y = -point.y * height + height / 2.0;
        return new Vector3d(x, y, point.z);
    }
}
