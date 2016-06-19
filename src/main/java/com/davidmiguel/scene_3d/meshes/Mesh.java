package com.davidmiguel.scene_3d.meshes;

import javax.vecmath.Vector3d;

/**
 * Mesh (3D object).
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
@SuppressWarnings("WeakerAccess")
public class Mesh {

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

    public Mesh() {
        this.vertices = new Vector3d[0];
        this.position = new Vector3d();
        this.rotation = new Vector3d();
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

    public Vector3d getRotation() {
        return rotation;
    }
}
