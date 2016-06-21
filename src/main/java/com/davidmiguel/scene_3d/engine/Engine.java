package com.davidmiguel.scene_3d.engine;

import com.davidmiguel.scene_3d.meshes.Face;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.meshes.Vertex;
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
        WIREFRAME, SHADING, TEXTURE
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
        for (double[] row : depthBuffer) {
            Arrays.fill(row, Double.MAX_VALUE);
        }
    }


    /**
     * Re-compute each vertex projection during each frame.
     */
    private void render(Camera camera, Mesh[] meshes, RenderMode mode) {
        Matrix4d viewMatrix = MathUtils.lookAtLH(camera.getPosition(), camera.getTarget(), MathUtils.UP);
        Matrix4d projectionMatrix = MathUtils.perspectiveFovLH(0.78, width / height, 0.01, 1.0);

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
                Vertex vertexA = mesh.getVertices()[face.getA()];
                Vertex vertexB = mesh.getVertices()[face.getB()];
                Vertex vertexC = mesh.getVertices()[face.getC()];
                Vertex pixelA = this.project(vertexA, transformMatrix, worldMatrix);
                Vertex pixelB = this.project(vertexB, transformMatrix, worldMatrix);
                Vertex pixelC = this.project(vertexC, transformMatrix, worldMatrix);
                // Draw
                switch (mode) {
                    case WIREFRAME:
                        DrawUtils.drawTriangle(backBuffer, pixelA, pixelB, pixelC, Color.WHITE);
                        break;
                    case SHADING:
                        DrawUtils.drawFilledTriangle(backBuffer, depthBuffer, pixelA, pixelB, pixelC,
                                new Color(1.0, 1.0, 1.0, 1.0));
                        break;
                    case TEXTURE:
                        DrawUtils.drawFilledTriangle(backBuffer, depthBuffer, pixelA, pixelB, pixelC,
                                new Color(1.0, 1.0, 1.0, 1.0), mesh.getTexture());
                        break;
                }
            }
        }
    }


    /**
     * Project takes some 3D coordinates and transform them in
     * 2D coordinates using the transformation matrix.
     */
    private Vertex project(Vertex vertex, Matrix4d transMat, Matrix4d world) {
        // Transforming the coordinates into 2D space
        Vector3d point2d = MathUtils.transformCoordinates(vertex.getCoordinates(), transMat);
        // Transforming  the coordinates & the normal to the vertex in the 3D world
        Vector3d point3dWorld = MathUtils.transformCoordinates(vertex.getCoordinates(), world);
        Vector3d normal3dWorld = MathUtils.transformCoordinates(vertex.getNormal(), world);
        // Transform from coordinate system starting at center to another starting at top left
        double x = point2d.x * width + width / 2.0;
        double y = -point2d.y * height + height / 2.0;
        return new Vertex(new Vector3d(x, y, point2d.z), point3dWorld, normal3dWorld, vertex.getTextureCoordinates());
    }
}
