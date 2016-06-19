package com.davidmiguel.scene_3d.engine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * Matrix operation helpers.
 * Based on babylon.math.js.
 *
 * @author davidmigloz
 * @since 19/06/2016
 */
@SuppressWarnings("WeakerAccess")
public class MathUtils {

    /**
     * Vector3 with y equal 1 anx x, z equal 0.
     */
    public static final Vector3d UP = new Vector3d(0, 1.0, 0);


    /**
     * Builds a left-handed look-at Matrix.
     *
     * @param eye    camera point
     * @param target camera look-at target
     * @param up     up direction
     * @return left-handed look-at Matrix
     */
    public static Matrix4d lookAtLH(Vector3d eye, Vector3d target, Vector3d up) {
        Vector3d zAxis = new Vector3d();
        zAxis.sub(target, eye);
        zAxis.normalize();
        Vector3d xAxis = new Vector3d();
        xAxis.cross(up, zAxis);
        xAxis.normalize();
        Vector3d yAxis = new Vector3d();
        yAxis.cross(zAxis, xAxis);
        yAxis.normalize();
        double ex = -xAxis.dot(eye);
        double ey = -yAxis.dot(eye);
        double ez = -zAxis.dot(eye);
        // Construct matrix
        double[] matrix = new double[]{
                xAxis.x, yAxis.x, zAxis.x, 0,
                xAxis.y, yAxis.y, zAxis.y, 0,
                xAxis.z, yAxis.z, zAxis.z, 0,
                ex, ey, ez, 1};
        return new Matrix4d(matrix);
    }

    /**
     * Transforms a vector into a new vector multiplying it by the transformation matrix.
     *
     * @param coord    vector to project
     * @param transMat transformation matrix
     * @return resulting vector
     */
    public static Vector3d transformCoordinates(Vector3d coord, Matrix4d transMat) {
        double x = (coord.x * transMat.m00) + (coord.y * transMat.m10) + (coord.z * transMat.m20) + transMat.m30;
        double y = (coord.x * transMat.m01) + (coord.y * transMat.m11) + (coord.z * transMat.m21) + transMat.m31;
        double z = (coord.x * transMat.m02) + (coord.y * transMat.m12) + (coord.z * transMat.m22) + transMat.m32;
        double w = (coord.x * transMat.m03) + (coord.y * transMat.m13) + (coord.z * transMat.m23) + transMat.m33;
        return new Vector3d(x / w, y / w, z / w);
    }

    /**
     * Creates a left-handed perspective projection matrix based on the field of view.
     * http://www.codinglabs.net/article_world_view_projection_matrix.aspx
     *
     * @param fov    field of view in the y direction, in radians
     * @param aspect aspect ratio, defined as the view space width divided by height
     * @param znear  z-value of the near view plane
     * @param zfar   z-value of the far view plane
     * @return projection matrix
     */
    public static Matrix4d perspectiveFovLH(double fov, double aspect, double znear, double zfar) {
        Matrix4d matrix = new Matrix4d();
        double tan = 1.0 / (Math.tan(fov * 0.5));
        matrix.m00 = tan / aspect;
        matrix.m11 = tan;
        matrix.m22 = -zfar / (znear - zfar);
        matrix.m23 = 1.0;
        matrix.m32 = (znear * zfar) / (znear - zfar);
        return matrix;
    }

    /**
     * Rotates a matrix using yaw, pitch and roll values.
     *
     * @param yaw   yaw rotation value
     * @param pitch pitch rotation value
     * @param roll  roll rotation value
     * @return rotated matrix
     */
    public static Matrix4d rotationYawPitchRoll(double yaw, double pitch, double roll) {
        Matrix4d m = MathUtils.rotationZ(roll);
        m.mul(MathUtils.rotationX(pitch));
        m.mul(MathUtils.rotationY(yaw));
        return m;
    }

    /**
     * Rotates a matrix around X axis.
     *
     * @param angle angle of the rotation
     * @return rotated matrix
     */
    public static Matrix4d rotationX(double angle) {
        Matrix4d result = new Matrix4d();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        result.m00 = 1.0;
        result.m11 = c;
        result.m12 = s;
        result.m21 = -s;
        result.m22 = c;
        result.m33 = 1.0;
        return result;
    }

    /**
     * Rotates a matrix around Y axis.
     *
     * @param angle angle of the rotation
     * @return rotated matrix
     */
    public static Matrix4d rotationY(double angle) {
        Matrix4d result = new Matrix4d();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        result.m00 = c;
        result.m02 = -s;
        result.m11 = 1.0;
        result.m20 = s;
        result.m22 = c;
        result.m33 = 1.0;
        return result;
    }

    /**
     * Rotates a matrix around Z axis.
     *
     * @param angle angle of the rotation
     * @return rotated matrix
     */
    public static Matrix4d rotationZ(double angle) {
        Matrix4d result = new Matrix4d();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        result.m00 = c;
        result.m01 = s;
        result.m10 = -s;
        result.m11 = c;
        result.m22 = 1.0;
        result.m33 = 1.0;
        return result;
    }

    /**
     * Creates a matrix with a translation pitch defined by x, y, z.
     *
     * @param x X translation value
     * @param y Y translation value
     * @param z Z translation value
     * @return new matrix
     */
    public static Matrix4d translation(double x, double y, double z) {
        Matrix4d result = new Matrix4d();
        result.setIdentity();
        result.m30 = x;
        result.m31 = y;
        result.m32 = z;
        return result;
    }
}
