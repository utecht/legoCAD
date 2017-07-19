/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import gnu.gleem.linalg.*;

/** Piece of geometry defining a two-way arrow, used in Translate1 and
    Translate2 manips. */

public class ManipPartTwoWayArrow extends ManipPartTriBased {
	private static final Vec3f[] vertices = {
		// Left tetrahedron
		new Vec3f(-1.0f, 0.0f, 0.0f),
		new Vec3f(-0.666666f, 0.166666f, 0.166666f),
		new Vec3f(-0.666666f, -0.166666f, 0.166666f),
		new Vec3f(-0.666666f, -0.166666f, -0.166666f),
		new Vec3f(-0.666666f, 0.166666f, -0.166666f),

		// Box at center
		new Vec3f(-0.666666f, 0.041666f, 0.0416666f),
		new Vec3f(-0.666666f, -0.041666f, 0.0416666f),
		new Vec3f(-0.666666f, -0.041666f, -0.0416666f),
		new Vec3f(-0.666666f, 0.041666f, -0.0416666f),
		new Vec3f(0.666666f, 0.041666f, 0.0416666f),
		new Vec3f(0.666666f, -0.041666f, 0.0416666f),
		new Vec3f(0.666666f, -0.041666f, -0.0416666f),
		new Vec3f(0.666666f, 0.041666f, -0.0416666f),

		// Right tetrahedron
		new Vec3f(0.666666f, 0.166666f, 0.166666f),
		new Vec3f(0.666666f, 0.166666f, -0.166666f),
		new Vec3f(0.666666f, -0.166666f, -0.166666f),
		new Vec3f(0.666666f, -0.166666f, 0.166666f),
		new Vec3f(1.0f, 0.0f, 0.0f),
	};

	private static final int[] vertexIndices = {
		// Left tetrahedron
		1, 0, 2,
		2, 0, 3,
		3, 0, 4,
		4, 0, 1,
		1, 2, 3,
		1, 3, 4,

		// Box
		5, 7, 6,   // left face
		5, 8, 7,
		5, 6, 10,  // front face
		5, 10, 9,
		6, 7, 11,  // bottom face
		6, 11, 10,
		7, 8, 12,  // back face
		7, 12, 11,
		8, 5, 9,   // top face
		8, 9, 12,
		9, 10, 11, // right face
		9, 11, 12,

		// Right tetrahedron
		13, 14, 15,
		13, 15, 16,
		17, 14, 13,
		17, 15, 14,
		17, 16, 15,
		17, 13, 16
	};

	private static Vec3f[] normals  = null;
	private static int[] normalIndices = null;

	public ManipPartTwoWayArrow() {
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
}
