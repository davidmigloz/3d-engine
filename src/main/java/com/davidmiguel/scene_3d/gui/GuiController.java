package com.davidmiguel.scene_3d.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
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

    @FXML
    private void initialize() {
        leftStatus.setText("Ready!");
        rightStatus.setText("0fps");
    }

    public GraphicsContext getGC() {
        return canvas.getGraphicsContext2D();
    }
}
