package com.davidmiguel.scene_3d.messes;

import javax.vecmath.Vector3d;

/**
 * Mess (3D object).
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class Mess {

    /**
     * Name of the 3D object.
     */
    private String name;

    /**
     * Collection of vertices (several vertex or 3D points).
     */
    private Vector3d[] vertices;

    /**
     * Its position in the 3D world.
     */
    private Vector3d position;

    /**
     * Its rotation state.
     */
    private Vector3d rotation;

    public Mess(String name, int numVertices) {
        this.name = name;
        this.vertices = new Vector3d[0];
        this.position = new Vector3d();
        this.rotation = new Vector3d();
    }

    public String getName() {
        return name;
    }

    public Vector3d[] getVertices() {
        return vertices;
    }

    void setVertices(Vector3d[] vertices) {
        this.vertices = vertices;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
    }
}
