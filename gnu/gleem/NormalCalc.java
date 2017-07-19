/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import gnu.gleem.linalg.*;

/** Calculates normals for a set of polygons. */

public class NormalCalc {

  /** Set of normals computed using {@link gnu.gleem.NormalCalc}. */
  public static class NormalInfo {
    public Vec3f[] normals;
    public int[]   normalIndices;

    NormalInfo(Vec3f[] normals, int[] normalIndices) {
      this.normals = normals;
      this.normalIndices = normalIndices;
    }
  }

  /** Returns null upon failure, or a set of Vec3fs and integers
      which represent faceted (non-averaged) normals, but per-vertex.
      Performs bounds checking on indices with respect to vertex list.
      Index list must represent independent triangles; indices are
      taken in groups of three. If index list doesn't represent
      triangles or other error occurred then returns null. ccw flag
      indicates whether triangles are specified counterclockwise when
      viewed from top or not. */

  public static NormalInfo computeFacetedNormals(Vec3f[] vertices,
                                                 int[] indices,
                                                 boolean ccw) {
    if ((indices.length % 3) != 0) {
      System.err.println("NormalCalc.computeFacetedNormals: numIndices wasn't " +
                         "divisible by 3, so it can't possibly " +
                         "represent a set of triangles");
      return null;
    }
    
    Vec3f[] outputNormals  = new Vec3f[indices.length / 3];
    int[] outputNormalIndices = new int[indices.length];

    Vec3f d1 = new Vec3f();
    Vec3f d2 = new Vec3f();
    int curNormalIndex = 0;
    for (int i = 0; i < indices.length; i += 3) {
      int i0 = indices[i];
      int i1 = indices[i+1];
      int i2 = indices[i+2];
      if ((i0 < 0) || (i0 >= indices.length) ||
	  (i1 < 0) || (i1 >= indices.length) ||
	  (i2 < 0) || (i2 >= indices.length)) {
	  System.err.println("NormalCalc.computeFacetedNormals: ERROR: " +
                             "vertex index out of bounds or no end of triangle " +
                             "index found");
          return null;
        }
      Vec3f v0 = vertices[i0];
      Vec3f v1 = vertices[i1];
      Vec3f v2 = vertices[i2];
      d1.sub(v1, v0);
      d2.sub(v2, v0);
      Vec3f n = new Vec3f();
      if (ccw) {
        n.cross(d1, d2);
      } else {
        n.cross(d2, d1);
      }
      n.normalize();
      outputNormals[curNormalIndex] = n;
      outputNormalIndices[i] = curNormalIndex;
      outputNormalIndices[i+1] = curNormalIndex;
      outputNormalIndices[i+2] = curNormalIndex;
      curNormalIndex++;
    }
    return new NormalInfo(outputNormals, outputNormalIndices);
  }
}
