package com.davidmiguel.engine_3d.meshes;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Vertex of a mesh.
 *
 * @author davidmigloz
 * @since 21/06/2016
 */
public class Vertex {
    private Vector3d coordinates;
    private Vector3d worldCoordinates;
    private Vector3d normal; // normal vector
    private Vector2d textureCoordinates;

    public Vertex(Vector3d coordinates, Vector3d worldCoordinates, Vector3d normal, Vector2d textureCoordinates) {
        this.coordinates = coordinates;
        this.worldCoordinates = worldCoordinates;
        this.normal = normal;
        this.textureCoordinates = textureCoordinates;
    }

    public Vertex(Vector3d coordinates, Vector3d normal) {
        this.coordinates = coordinates;
        this.normal = normal;
    }

    public Vector3d getNormal() {
        return normal;
    }

    public Vector3d getCoordinates() {
        return coordinates;
    }

    public Vector3d getWorldCoordinates() {
        return worldCoordinates;
    }

    public Vector2d getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates(Vector2d textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }
}
