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
     * Flush the back buffer into the canvas.
     */
    public void draw(Camera camera, Mesh[] meshes) {
        if (meshes == null || meshes.length == 0 || camera == null) {
            return;
        }
        // Clear the screen and all associated pixels with white ones
        this.clear();
        // Render them into the back buffer by doing the required matrix operations
        this.render(camera, meshes);
        // Display them on screen by flushing the back buffer data into the front buffer
        gc.drawImage(backBuffer, 0, 0);
    }

    /**
     * This function is called to clear the back buffer and canvas.
     */
    private void clear() {
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
    private void render(Camera camera, Mesh[] meshes) {
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
                // Draw triangle
                double color = (0.25
                        + ((indexFaces % mesh.getFaces().length)
                        / (double) mesh.getFaces().length)
                        * 0.75);
                DrawUtils.drawTriangle(backBuffer, pixelA, pixelB, pixelC, new Color(color, color, color, 1));
                indexFaces++;
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
        double x = point.x * gc.getCanvas().getWidth() + gc.getCanvas().getWidth() / 2.0;
        double y = -point.y * gc.getCanvas().getHeight() + gc.getCanvas().getHeight() / 2.0;
        return new Vector3d(x, y, point.z);
    }
}
