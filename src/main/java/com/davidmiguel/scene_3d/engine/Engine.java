package com.davidmiguel.scene_3d.engine;

/**
 * 3D engine.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Engine {

    private Camera camera;

    public Engine() {
        this.camera = new Camera(new double[] {10, 10, 10}, new double[] {0, 0, 0});
    }
}
