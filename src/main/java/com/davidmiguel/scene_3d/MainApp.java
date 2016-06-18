package com.davidmiguel.scene_3d;

import com.davidmiguel.scene_3d.engine.Engine;
import com.davidmiguel.scene_3d.engine.Polygon;
import com.davidmiguel.scene_3d.gui.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main App.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/gui.fxml"));
            Parent root = loader.load();
            // Config layout
            primaryStage.setTitle("3D Scene");
            primaryStage.setResizable(false);
            // Create scene
            final Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            // Get controller and lunch 3d engine
            GuiController controller = loader.getController();
            Engine engine = new Engine(controller.getGC());
            controller.setEngine(engine);
            // Lunch GUI
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading GUI", e);
        }
    }
}
