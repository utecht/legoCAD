/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import gnu.gleem.linalg.*;

/** A cube of width, height, and depth 2, centered about the origin
    and aligned with the X, Y, and Z axes. */

public class ManipPartCube extends ManipPartTriBased {
  private static final Vec3f[] vertices = {
    // Front side
    new Vec3f(-1, 1, 1),
    new Vec3f(-1, -1, 1),
    new Vec3f(1, -1, 1),
    new Vec3f(1, 1, 1),
    // Back side
    new Vec3f(-1, 1, -1),
    new Vec3f(-1, -1, -1),
    new Vec3f(1, -1, -1),
    new Vec3f(1, 1, -1),
  };

  private static final int[] vertexIndices = {
    // Front face
    0, 1, 2,
    0, 2, 3,
    // Right face
    3, 2, 6,
    3, 6, 7,
    // Back face
    7, 6, 5,
    7, 5, 4,
    // Left face
    4, 5, 1,
    4, 1, 0,
    // Top face
    4, 0, 3,
    4, 3, 7,
    // Bottom face
    1, 5, 6,
    1, 6, 2
  };

  private static Vec3f[] normals  = null;
  private static int[] normalIndices = null;

  public ManipPartCube() {
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
