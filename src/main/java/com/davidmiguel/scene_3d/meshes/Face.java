package com.davidmiguel.scene_3d.meshes;

/**
 * Triangle face of a 3d object. It contains the three vertices of the mesh that form a triangle.
 *
 * @author davidmigloz
 * @since 19/06/2016
 */
@SuppressWarnings("WeakerAccess")
public class Face {

    private int a;
    private int b;
    private int c;

    public Face(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }
}
