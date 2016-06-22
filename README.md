# 3d engine

Software implementation (based on CPU) of a 3D engine.

![Screenshot](https://raw.githubusercontent.com/davidmigloz/3d-engine/master/docs/img/screenshot.gif)

#### Features

- Three types of rendering (wireframe, shading and texture).
- User has the control over the camera.
- Presentation mode (the model rotates automatically).
- 8 3D models included:
  + Cube
	+ UV Sphere
	+ ICO Sphere
	+ Cylinder
	+ Cone 
	+ Torus
	+ Suzzane
- Import 3D models in [Babylon JSON](https://doc.babylonjs.com/generals/File_Format_Map_(.babylon)) format.
- Models can be exported from Blender with [Babylon exporter plug-in](david.blob.core.windows.net/softengine3d/io_export_babylon.py).

#### Details

- Left-handed world.
- Rendering loop: 60hz.
- Lines are drawn with Bresenham’s algorithm.
- Triangles are filled with scan-line algorithm.
- Visibility problem is solved with Z-Buffer.
- Gouraud Shading for handling lightning.
- Fixed point of light.
- UV coordinates for texture mapping.
- Data loaded from Babylon JSON files:
  + Mesh:
	  - Name
		- Vertices
		- Faces
		- Position
		- Rotation
		- Texture
		- UV coordinates
	+ Texture:
		- Name 
		- ID
		- Image file name

#### Download

> [engine-3d.v1.jar](https://github.com/davidmigloz/engine-3d/releases/download/v1/engine-3d.v1.jar)

#### References

1. https://www.davrous.com/2013/06/13/tutorial-series-learning-how-to-write-a-3d-soft-engine-from-scratch-in-c-typescript-or-javascript/
2. http://www.codinglabs.net/article_world_view_projection_matrix.aspx
3. http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
4. http://blog.db-in.com/cameras-on-opengl-es-2-x/
5. https://msdn.microsoft.com/en-us/library/windows/desktop/bb206269(v=vs.85).aspx
6. http://inear.se/talk/a_brief_introduction_to_3d.pptx
7. http://www.songho.ca/opengl/gl_transform.html
8. https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
9. https://en.wikipedia.org/wiki/Scanline_rendering
10. http://doc.babylonjs.com/
11. https://en.wikipedia.org/wiki/Z-buffering
12. https://en.wikipedia.org/wiki/Gouraud_shading
13. https://en.wikipedia.org/wiki/Normal_(geometry)/
14. https://en.wikipedia.org/wiki/Dot_product
15. https://en.wikipedia.org/wiki/Cross_product
16. http://www.bencloward.com/tutorials_normal_maps2.shtml
17. https://en.wikipedia.org/wiki/Texture_mapping
18. http://ogldev.atspace.co.uk/www/tutorial16/tutorial16.html
19. https://www.blender.org/manual/render/index.html
20. http://www.real3dtutorials.com/tut00005.php