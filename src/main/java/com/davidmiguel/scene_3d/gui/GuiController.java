package com.davidmiguel.scene_3d.gui;

import com.davidmiguel.scene_3d.engine.Engine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Engine engine;

    @FXML
    private void initialize() {
        leftStatus.setText("Ready!");
        rightStatus.setText("0fps");
        canvas.getGraphicsContext2D().setLineWidth(1);
    }

    public GraphicsContext getGC() {
        return canvas.getGraphicsContext2D();
    }

    public void setEngine(Engine engine) {
        this.engine = engine;

        // Rendering loop (50hz)
        Timeline tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.millis(20), event -> {
                    // TODO Draw
                });
        tl.getKeyFrames().add(frame);
        tl.play();
    }
}
