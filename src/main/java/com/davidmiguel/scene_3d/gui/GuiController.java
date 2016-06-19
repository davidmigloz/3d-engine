package com.davidmiguel.scene_3d.gui;

import com.davidmiguel.scene_3d.engine.Camera;
import com.davidmiguel.scene_3d.engine.Engine;
import com.davidmiguel.scene_3d.meshes.Mesh;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * GuiController.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class GuiController {

    @FXML
    private Canvas canvas;
    @FXML
    private Label leftStatus;
    @FXML
    private Label rightStatus;

    @FXML
    private void initialize() {
        leftStatus.setText("Ready!");
        rightStatus.setText("0fps");
        canvas.getGraphicsContext2D().setLineWidth(1);
    }

    public GraphicsContext getGC() {
        return canvas.getGraphicsContext2D();
    }

    public void run(Engine engine, Camera camera, Mesh[] meshes) {
        // Rendering loop (50hz)
        Timeline tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.millis(20), event -> {
            // Clear the screen and all associated pixels with white ones
            engine.clear();
            //  Update the various position & rotation values of our meshes
            for (Mesh mesh : meshes) {
                mesh.getRotation().x += 0.01;
                mesh.getRotation().y += 0.01;
            }
            // Render them into the back buffer by doing the required matrix operations
            engine.render(camera, meshes);
            // Display them on screen by flushing the back buffer data into the front buffer
            engine.draw();
        });
        tl.getKeyFrames().add(frame);
        tl.play();
    }
}
