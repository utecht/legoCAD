/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Wraps a 3D point and parametric time value. */

public class IntersectionPoint {
  private Vec3f intPt = new Vec3f();
  private float t;

  public Vec3f getIntersectionPoint() {
    return intPt;
  }

  public void setIntersectionPoint(Vec3f newPt) {
    intPt.set(newPt);
  }

  public float getT() {
    return t;
  }

  public void setT(float t) {
    this.t = t;
  }
}
