package com.davidmiguel.scene_3d.utils;

import com.davidmiguel.scene_3d.meshes.Face;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Vector3d;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods to manage the import of models.
 *
 * @author davidmigloz
 * @since 19/06/2016
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static Mesh[] parseMeshFromJSON(File json) {
        List<Mesh> meshes = new ArrayList<>();
        try {
            // Get parser
            JsonReader reader = new JsonReader(new FileReader(json));
            // Read JSON
            reader.beginArray();
            while (reader.hasNext()) {
                meshes.add(readMesh(reader));
            }
            reader.endArray();


        } catch (IOException e) {
            logger.error("Error at parsing JSON.", e);
        }
        return meshes.toArray(new Mesh[meshes.size()]);
    }

    private static Mesh readMesh(JsonReader reader) throws IOException {
        List<Double> verticesList = new ArrayList<>(0);
        List<Integer> facesList = new ArrayList<>(0);
        List<Double> positionList = new ArrayList<>(0);
        int uvCount = 0;

        // Parse object
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "vertices":
                    verticesList = readDoubleArray(reader);
                    break;
                case "indices":
                    facesList = readListIntegers(reader);
                    break;
                case "position":
                    positionList = readDoubleArray(reader);
                    break;
                case "uvCount":
                    uvCount = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        // Build mesh
        int verticesStep = 1; // vertices array step depends on the number of texture's coordinates per vertex
        switch (uvCount) {
            case 0:
                verticesStep = 6;
                break;
            case 1:
                verticesStep = 8;
                break;
            case 2:
                verticesStep = 10;
                break;
        }
        // Number of vertices
        int numVertices = verticesList.size() / verticesStep;
        // Number of faces (size of the array divided by 3 (a face is a triangle))
        int numFaces = facesList.size() / 3;
        // Create mesh
        Mesh mesh = new Mesh();
        // Add vertices
        Vector3d[] vertices = new Vector3d[numVertices];
        for (int i = 0; i < numVertices; i++) {
            double x = verticesList.get(i * verticesStep);
            double y = verticesList.get(i * verticesStep + 1);
            double z = verticesList.get(i * verticesStep + 2);
            vertices[i] = new Vector3d(x, y, z);
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
        return mesh;
    }

    /**
     * Read list of doubles from JSON array.
     */
    private static List<Double> readDoubleArray(JsonReader reader) throws IOException {
        List<Double> vertices = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            vertices.add(reader.nextDouble());
        }
        reader.endArray();

        return vertices;
    }

    /**
     * Read list of integers from JSON array.
     */
    private static List<Integer> readListIntegers(JsonReader reader) throws IOException {
        List<Integer> faces = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            faces.add(reader.nextInt());
        }
        reader.endArray();

        return faces;
    }
}
