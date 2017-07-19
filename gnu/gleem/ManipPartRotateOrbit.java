package gnu.gleem;

import gnu.gleem.linalg.Mat4f;
import gnu.gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;



public class ManipPartRotateOrbit extends ManipPartTriBased
{
	private static final float WIDTH = .1f;
	private static final int NUM_TRIANGLES = 100;
	private static final Vec3f[] vertices = createVertices();

	private static final int[] vertexIndices = createIndices();

	private static Vec3f[] normals  = null;
	private static int[] normalIndices = null;

	public ManipPartRotateOrbit() {
		super();
		if (normals == null) {
			NormalCalc.NormalInfo normInfo =
				NormalCalc.computeFacetedNormals(vertices, vertexIndices, true);
			normals = normInfo.normals;
			normalIndices = normInfo.normalIndices;
		}

		setVertices(vertices);
		setVertexIndices(vertexIndices);
		setNormals(normals);
		setNormalIndices(normalIndices);
	}
	private static Vec3f[] createVertices()
	{
		float theta = (float)(2*Math.PI / NUM_TRIANGLES);
		Vec3f[] verts = new Vec3f[(NUM_TRIANGLES+2)*3];

		for(int i = 0; i < (NUM_TRIANGLES+2)*3; i+=6)
		{
			Vec3f zero = getVert(theta*i, true);
			Vec3f one = getVert(theta*i, false);
			Vec3f two = getVert(theta*(i+1), true);
			Vec3f three = getVert(theta*(i+1), false);

			verts[i] = zero;
			verts[i+1] = one;
			verts[i+2] = two;

			verts[i+3] = one;
			verts[i+4] = two;
			verts[i+5] = three;

		}


		return verts;
	}
	private static Vec3f getVert(float theta, boolean inner)
	{
		float radius = 1;
		if(inner) radius = 1-WIDTH;

		return new Vec3f((float)(radius*Math.cos(theta)), (float)(radius*Math.sin(theta)), 0);
	}
	private static int[] createIndices()
	{
		int[] indices = new int[(NUM_TRIANGLES+2)*3];
		int zero = 0;
		int one = 1;
		int two = 2;
		int three = 3;
		int four = 4;
		for(int i = 0; i < (NUM_TRIANGLES+2)*3; i+=6)
		{
			indices[i] = zero;
			indices[i+1] = one;
			indices[i+2] = two;
			indices[i+3] = one;
			indices[i+4] = two;
			indices[i+5] = three;

			zero += 2;
			one += 2;
			two += 2;
			three += 2;
			four += 2;

		}


		return indices;
	}
}
