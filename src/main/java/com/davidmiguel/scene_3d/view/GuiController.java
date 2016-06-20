package com.davidmiguel.scene_3d.view;

import com.davidmiguel.scene_3d.engine.Camera;
import com.davidmiguel.scene_3d.engine.Engine;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.utils.FileUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.vecmath.Vector3d;
import java.io.File;
import java.net.URISyntaxException;

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
    private Label status;
    @FXML
    private Label fps;
    @FXML
    private ToggleGroup render;
    @FXML
    private MenuItem play;


    private Timeline tl;
    private boolean rotation;
    private Engine.RenderMode selectedRender;
    private Engine engine;
    private Mesh[] meshes;
    private Camera camera;
    private static long t0;

    @FXML
    private void initialize() {
        // Initial setup
        canvas.getGraphicsContext2D().setLineWidth(1);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        rotation = false;
        selectedRender = Engine.RenderMode.RASTERIZATION;
        // Config 3d engine
        engine = new Engine(canvas.getGraphicsContext2D());
        meshes = new Mesh[0];
        camera = new Camera(new Vector3d(0, 0, 10), new Vector3d(0, 0, 0));
        setupRenderingLoop();
        status.setText("Ready! Open mesh...");
    }

    @FXML
    private void handleOpenMesh() {
        Stage primaryStage = (Stage) canvas.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON (*.json;*.babylon)", "*.json", "*.babylon"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Open mesh...");
        File f = fileChooser.showOpenDialog(primaryStage);
        if (f != null) {
            addMeshesFromFile(f);
        }
    }

    @FXML
    private void handleOpenCube() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/Cube.json").toURI()));
    }

    @FXML
    private void handleOpenUVSphere() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/UVSphere.json").toURI()));
    }

    @FXML
    private void handleOpenICOSphere() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/ICOSphere.json").toURI()));
    }

    @FXML
    private void handleOpenCylinder() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/Cylinder.json").toURI()));
    }

    @FXML
    private void handleOpenCone() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/Cone.json").toURI()));
    }

    @FXML
    private void handleOpenTorus() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/Torus.json").toURI()));
    }

    @FXML
    private void handleOpenSuzanne() throws URISyntaxException {
        this.addMeshesFromFile(new File(getClass().getResource("/meshes/Suzanne.json").toURI()));
    }

    @FXML
    private void handlePlay() {
        if (rotation) {
            rotation = false;
            play.setText("Play");
        } else {
            rotation = true;
            play.setText("Pause");
        }
    }

    @FXML
    private void handleSelectRender() {
        for (Toggle t : render.getToggles()) {
            if (t.isSelected()) {
                switch (((RadioMenuItem) t).getText()) {
                    case "Wireframe":
                        selectedRender = Engine.RenderMode.WIREFRAME;
                        break;
                    case "Rasterization":
                        selectedRender = Engine.RenderMode.RASTERIZATION;
                        break;
                }
                break;
            }
        }
    }

    @FXML
    private void handleCameraUp() {
        camera.getPosition().y -= 0.05;
        camera.getTarget().y -= 0.05;
    }

    @FXML
    private void handleCameraDown() {
        camera.getPosition().y += 0.05;
        camera.getTarget().y += 0.05;
    }

    @FXML
    private void handleCameraRight() {
        camera.getPosition().x += 0.05;
        camera.getTarget().x += 0.05;
    }

    @FXML
    private void handleCameraLeft() {
        camera.getPosition().x -= 0.05;
        camera.getTarget().x -= 0.05;
    }

    @FXML
    private void handleCameraForward() {
        camera.getPosition().z -= 0.2;
        camera.getTarget().z -= 0.2;
    }

    @FXML
    private void handleCameraBackward() {
        camera.getPosition().z += 0.2;
        camera.getTarget().z += 0.2;
    }

    @FXML
    private void handleExit() {
        tl.stop();
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("IDEA cipher");
        alert.setHeaderText("About");
        alert.setContentText("Author: David Miguel\nWebsite: http://davidmiguel.com/");
        alert.showAndWait();
    }

    private void addMeshesFromFile(File f) {
        meshes = FileUtils.parseMeshFromJSON(f);
        status.setText(f.getName() + " loaded!");
        // Start rendering loop, start rotation and reset camera
        tl.play();
        if(!rotation) {
            handlePlay();
        }
        camera = new Camera(new Vector3d(0, 0, 10), new Vector3d(0, 0, 0));
    }

    private void setupRenderingLoop() {
        // Rendering loop (50hz)
        tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        t0 = System.currentTimeMillis(); // For computation of fps
        KeyFrame frame = new KeyFrame(Duration.millis(20), event -> {
            //  Update the various position & rotation values of our meshes
            if(rotation) {
                for (Mesh mesh : meshes) {
                    mesh.getRotation().x += 0.01;
                    mesh.getRotation().y += 0.01;
                }
            }
            // Draw frame
            engine.draw(camera, meshes, selectedRender);
            // Update fps info
            long t1 = System.currentTimeMillis();
            fps.setText(Long.toString(Math.round(1 / ((t1 - t0) / 1000.0))));
            t0 = t1;
        });
        tl.getKeyFrames().add(frame);
    }
}
