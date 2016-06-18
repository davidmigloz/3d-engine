package com.davidmiguel.scene_3d.engine;

import javax.vecmath.Vector3d;

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
    private Vector3d position;

    /**
     * Position of camera target expressed in coordinate system of the scene.
     */
    private Vector3d target;

    public Camera(Vector3d position, Vector3d cTarget) {
        this.position = position;
        this.target = cTarget;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getTarget() {
        return target;
    }

    public void setTarget(Vector3d target) {
        this.target = target;
    }
}
