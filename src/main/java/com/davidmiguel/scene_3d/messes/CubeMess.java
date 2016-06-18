package com.davidmiguel.scene_3d.messes;

import javax.vecmath.Vector3d;

/**
 * Class.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class CubeMess extends Mess {

    public CubeMess() {
        super("Cube", 8);
        // Add vertices
        Vector3d[] vertices = new Vector3d[8];
        vertices[0] = new Vector3d(-1, 1, 1);
        vertices[1] = new Vector3d(1, 1, 1);
        vertices[2] = new Vector3d(-1, -1, 1);
        vertices[3] = new Vector3d(-1, -1, -1);
        vertices[4] = new Vector3d(-1, 1, -1);
        vertices[5] = new Vector3d(1, 1, -1);
        vertices[6] = new Vector3d(1, -1, 1);
        vertices[7] = new Vector3d(1, -1, -1);
        setVertices(vertices);
    }
}
