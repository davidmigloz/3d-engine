package com.davidmiguel.scene_3d.meshes;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * Texture of a mesh.
 *
 * @author davidmigloz
 * @since 21/06/2016
 */
public class Texture {

    private PixelReader image;
    private int width;
    private int height;

    public Texture(Image image) {
        this.image = image.getPixelReader();
        width = (int) image.getWidth();
        height = (int) image.getHeight();
    }

    /**
     * Takes the U & V coordinates exported by Blender and return
     * the corresponding pixel color in the texture.
     */
    public Color map(double tu, double tv) {
        // Image is null
        if (image == null) {
            return Color.WHITE;
        }
        // Using a % operator to cycle/repeat the texture if needed
        int u = Math.abs((int) (tu * width) % width);
        int v = Math.abs((int) (tv * height) % height);
        return image.getColor(u, v);
    }
}
