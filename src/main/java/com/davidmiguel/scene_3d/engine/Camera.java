package com.davidmiguel.scene_3d.engine;

/**
 * Camera describes the point-of-view (position and orientation) used to observe the scene.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Camera {

    /**
     * Position of the camera expressed as a point in the coordinate system of the scene.
     */
    private double[] cPos;

    /**
     * Position of camera target expressed in coordinate system of the scene.
     */
    private double[] cTarget;

    public Camera(double[] cPos, double[] cTarget) {
        this.cPos = cPos;
        this.cTarget = cTarget;
    }

    public double[] getcPos() {
        return cPos;
    }

    public void setcPos(double[] cPos) {
        this.cPos = cPos;
    }

    public double[] getcTarget() {
        return cTarget;
    }

    public void setcTarget(double[] cTarget) {
        this.cTarget = cTarget;
    }
}
