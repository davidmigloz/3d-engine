package com.davidmiguel.engine_3d.meshes;

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
    private Vertex[] vertices;

    /**
     * Faces of the mesh.
     */
    private Face[] faces;

    /**
     * Its position in the 3D world.
     */
    private Vector3d position;

    /**
     * Its rotation state.
     */
    private Vector3d rotation;

    /**
     * Texture of the mesh.
     */
    private Texture texture;

    public Mesh() {
        this.vertices = new Vertex[0];
        this.faces = new Face[0];
        this.position = new Vector3d();
        this.rotation = new Vector3d();
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public Face[] getFaces() {
        return faces;
    }

    public void setFaces(Face[] faces) {
        this.faces = faces;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
