package com.davidmiguel.scene_3d.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Polygon.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Polygon {

    private double[] x;
    private double[] y;
    private Color color;

    public Polygon(double[] x, double[] y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(color);
        gc.strokePolygon(x, y, x.length);
    }
}
