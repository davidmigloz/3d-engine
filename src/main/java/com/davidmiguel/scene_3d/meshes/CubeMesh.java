package com.davidmiguel.scene_3d.meshes;

import javax.vecmath.Vector3d;

/**
 * Class.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class CubeMesh extends Mesh {

    public CubeMesh() {
        super();

        // Add vertices
        Vector3d[] vertices = new Vector3d[8];
        vertices[0] = new Vector3d(-1, 1, 1);
        vertices[1] = new Vector3d(1, 1, 1);
        vertices[2] = new Vector3d(-1, -1, 1);
        vertices[3] = new Vector3d(1, -1, 1);
        vertices[4] = new Vector3d(-1, 1, -1);
        vertices[5] = new Vector3d(1, 1, -1);
        vertices[6] = new Vector3d(1, -1, -1);
        vertices[7] = new Vector3d(-1, -1, -1);
        this.setVertices(vertices);

        // Add faces
        Face[] faces = new Face[12];
        faces[0] = new Face(0, 1, 2);
        faces[1] = new Face(1, 2, 3);
        faces[2] = new Face(1, 3, 6);
        faces[3] = new Face(1, 5, 6);
        faces[4] = new Face(0, 1, 4);
        faces[5] = new Face(1, 4, 5);

        faces[6] = new Face(2, 3, 7);
        faces[7] = new Face(3, 6, 7);
        faces[8] = new Face(0, 2, 7);
        faces[9] = new Face(0, 4, 7);
        faces[10] = new Face(4, 5, 6);
        faces[11] = new Face(4, 6, 7);
        this.setFaces(faces);
    }
}
