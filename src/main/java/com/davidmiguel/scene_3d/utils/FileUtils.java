package com.davidmiguel.scene_3d.utils;

import com.davidmiguel.scene_3d.meshes.Face;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.meshes.Texture;
import com.davidmiguel.scene_3d.meshes.Vertex;
import com.google.gson.stream.JsonReader;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods to manage the import of meshes from file.
 *
 * @author davidmigloz
 * @since 19/06/2016
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Import a mesh from a babylon json file.
     * https://doc.babylonjs.com/generals/File_Format_Map_(.babylon)
     *
     * @param file json file
     * @return array of meshes
     */
    public static Mesh[] parseMeshFromJSON(InputStream file) {
        List<Mesh> meshes = new ArrayList<>();
        Map<String, Material> materials = new HashMap<>();

        try (JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(file)))){
            // Read JSON
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "materials":
                        reader.beginArray();
                        Material m = readMaterial(reader);
                        if (m != null) {
                            materials.put(m.getId(), m);
                        }
                        reader.endArray();
                        break;
                    case "meshes":
                        reader.beginArray();
                        meshes.add(readMesh(reader, materials));
                        reader.endArray();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            logger.error("Error at parsing JSON.", e);
        }
        return meshes.toArray(new Mesh[meshes.size()]);
    }

    private static Material readMaterial(JsonReader reader) throws IOException {
        String name = null;
        String id = null;
        String diffuseTextureName = null;

        // Parse object
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "name":
                    name = reader.nextString();
                    break;
                case "id":
                    id = reader.nextString();
                    break;
                case "diffuseTexture":
                    diffuseTextureName = readDiffuseTexture(reader) ;
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return name != null ? new Material(name, id, diffuseTextureName) : null;
    }

    private static String readDiffuseTexture(JsonReader reader) throws IOException {
        String name = null;
        // Parse object
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "name":
                    name = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return name;
    }

    private static Mesh readMesh(JsonReader reader, Map<String, Material> materials) throws IOException {
        List<Double> verticesList = new ArrayList<>(0);
        List<Integer> facesList = new ArrayList<>(0);
        List<Double> positionList = new ArrayList<>(0);
        List<Double> rotationList = new ArrayList<>(0);
        int uvCount = 0;
        String materialId = null;

        // Parse object
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "vertices":
                    verticesList = readDoubleArray(reader);
                    break;
                case "indices":
                    facesList = readIntegerArray(reader);
                    break;
                case "position":
                    positionList = readDoubleArray(reader);
                    break;
                case "rotation":
                    rotationList = readDoubleArray(reader);
                    break;
                case "uvCount":
                    uvCount = reader.nextInt();
                    break;
                case "materialId":
                    materialId = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        // Build mesh
        int verticesStep; // vertices array step depends on the number of texture's coordinates per vertex
        switch (uvCount) {
            case 0:
                verticesStep = 6; // coord vector + normal vector
                break;
            case 1:
                verticesStep = 8; // coord vector + normal vector + UV coord
                break;
            case 2:
                verticesStep = 10;
                break;
            default:
                logger.error("Error at parsing JSON.");
                return null;
        }
        // Number of vertices
        int numVertices = verticesList.size() / verticesStep;
        // Number of faces (size of the array divided by 3 (a face is a triangle))
        int numFaces = facesList.size() / 3;
        // Create mesh
        Mesh mesh = new Mesh();
        // Add vertices
        Vertex[] vertices = new Vertex[numVertices];
        for (int i = 0; i < numVertices; i++) {
            // Vertex coord vector
            double x = verticesList.get(i * verticesStep);
            double y = verticesList.get(i * verticesStep + 1);
            double z = verticesList.get(i * verticesStep + 2);
            // Loading the vertex normal vector exported by Blender
            double nx = verticesList.get(i * verticesStep + 3);
            double ny = verticesList.get(i * verticesStep + 4);
            double nz = verticesList.get(i * verticesStep + 5);
            Vertex vertex = new Vertex(new Vector3d(x, y, z), new Vector3d(nx, ny, nz));
            // Loading the texture coordinates (UV)
            if (uvCount > 0) {
                // Loading the texture coordinates
                double u = verticesList.get(i * verticesStep + 6);
                double v = verticesList.get(i * verticesStep + 7);
                vertex.setTextureCoordinates(new Vector2d(u, v));
            }
            vertices[i] = vertex;
        }
        mesh.setVertices(vertices);
        // Add faces
        Face[] faces = new Face[numFaces];
        for (int i = 0; i < numFaces; i++) {
            int a = facesList.get(i * 3);
            int b = facesList.get(i * 3 + 1);
            int c = facesList.get(i * 3 + 2);
            faces[i] = new Face(a, b, c);
        }
        mesh.setFaces(faces);
        // Set position
        mesh.getPosition().x = positionList.get(0);
        mesh.getPosition().y = positionList.get(1);
        mesh.getPosition().z = positionList.get(2);
        // Set rotation
        mesh.getRotation().x = rotationList.get(0);
        mesh.getRotation().y = rotationList.get(1);
        mesh.getRotation().z = rotationList.get(2);
        // Set texture
        if (uvCount > 0) {
            String meshTextureName = materials.get(materialId).getDiffuseTextureName();
            InputStream in = FileUtils.class.getResourceAsStream("/textures/" + meshTextureName);
            mesh.setTexture(new Texture(new Image(in)));
        }
        return mesh;
    }

    /**
     * Read list of doubles from JSON array.
     */
    private static List<Double> readDoubleArray(JsonReader reader) throws IOException {
        List<Double> list = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(reader.nextDouble());
        }
        reader.endArray();

        return list;
    }

    /**
     * Read list of integers from JSON array.
     */
    private static List<Integer> readIntegerArray(JsonReader reader) throws IOException {
        List<Integer> list = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(reader.nextInt());
        }
        reader.endArray();

        return list;
    }
}
