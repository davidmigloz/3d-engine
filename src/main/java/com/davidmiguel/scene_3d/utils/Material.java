package com.davidmiguel.scene_3d.utils;

/**
 * Material.
 * Used to import the texture of a mesh.
 *
 * @author davidmigloz
 * @since 21/06/2016
 */
public class Material {
    private String name;
    private String id;
    private String diffuseTextureName;

    public Material(String name, String id, String diffuseTextureName) {
        this.name = name;
        this.id = id;
        this.diffuseTextureName = diffuseTextureName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDiffuseTextureName() {
        return diffuseTextureName;
    }
}
