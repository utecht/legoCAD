/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import gnu.gleem.linalg.*;

/** Represents a bounding sphere. */

public class BSphere {
  private Vec3f center = new Vec3f();
  private float radius;
  private float radSq;

  /** Default constructor creates a sphere with center (0, 0, 0) and
      radius 0 */
  public BSphere() {
    makeEmpty();
  }

  public BSphere(Vec3f center, float radius) {
    set(center, radius);
  }

  /** Re-initialize this sphere to center (0, 0, 0) and radius 0 */
  public void makeEmpty() {
    center.set(0, 0, 0);
    radius = radSq = 0;
  }

  public void  setCenter(Vec3f center) { this.center.set(center); }
  public Vec3f getCenter()             { return center;           }

  public void  setRadius(float radius) { this.radius = radius;
                                         radSq = radius * radius; }
  public float getRadius()             { return radius;           }

  public void set(Vec3f center, float radius) {
    setCenter(center); setRadius(radius); 
  }
  /** Returns radius and mutates passed "center" vector */
  float get(Vec3f center) {
    center.set(this.center); return radius;
  }

  /** Mutate this sphere to encompass both itself and the argument.
      Ignores zero-size arguments. */
  public void extendBy(BSphere arg) {
    if ((radius == 0.0f) || (arg.radius == 0.0f))
      return;
    // FIXME: This algorithm is a quick hack -- minimum bounding
    // sphere of a set of other spheres is a well studied problem, but
    // not by me
    Vec3f diff = arg.center.minus(center);
    if (diff.lengthSquared() == 0.0f) {
      setRadius(Math.max(radius, arg.radius));
      return;
    }
    IntersectionPoint[] intPt = new IntersectionPoint[4];
    for (int i = 0; i < intPt.length; i++) {
      intPt[i] = new IntersectionPoint();
    }
    int numIntersections;
    numIntersections = intersectRay(center, diff, intPt[0], intPt[1]);
    assert numIntersections == 2;
    numIntersections = intersectRay(center, diff, intPt[2], intPt[3]);
    assert numIntersections == 2;
    IntersectionPoint minIntPt = intPt[0];
    IntersectionPoint maxIntPt = intPt[0];
    // Find minimum and maximum t values, take associated intersection
    // points, find midpoint and half length of line segment -->
    // center and radius.
    for (int i = 0; i < 4; i++) {
      if (intPt[i].getT() < minIntPt.getT()) {
        minIntPt = intPt[i];
      } else if (intPt[i].getT() > maxIntPt.getT()) {
        maxIntPt = intPt[i];
      }
    }
    // Compute the average -- this is the new center
    center.add(minIntPt.getIntersectionPoint(),
               maxIntPt.getIntersectionPoint());
    center.scale(0.5f);
    // Compute half the length -- this is the radius
    setRadius(
      0.5f *
      minIntPt.getIntersectionPoint().
        minus(maxIntPt.getIntersectionPoint()).
          length()
    );
  }

  /** Intersect a ray with the sphere. This is a one-sided ray
      cast. Mutates one or both of intPt0 and intPt1. Returns number
      of intersections which occurred. */
  int intersectRay(Vec3f rayStart,
                   Vec3f rayDirection,
                   IntersectionPoint intPt0,
                   IntersectionPoint intPt1) {
    // Solve quadratic equation
    float a = rayDirection.lengthSquared();
    if (a == 0.0)
      return 0;
    float b = 2.0f * (rayStart.dot(rayDirection) - rayDirection.dot(center));
    Vec3f tempDiff = center.minus(rayStart);
    float c = tempDiff.lengthSquared() - radSq;
    float disc = b * b - 4 * a * c;
    if (disc < 0.0f)
      return 0;
    int numIntersections;
    if (disc == 0.0f)
      numIntersections = 1;
    else
      numIntersections = 2;
    intPt0.setT((0.5f * (-1.0f * b + (float) Math.sqrt(disc))) / a);
    if (numIntersections == 2)
      intPt1.setT((0.5f * (-1.0f * b - (float) Math.sqrt(disc))) / a);
    Vec3f tmp = new Vec3f(rayDirection);
    tmp.scale(intPt0.getT());
    tmp.add(tmp, rayStart);
    intPt0.setIntersectionPoint(tmp);
    if (numIntersections == 2) {
      tmp.set(rayDirection);
      tmp.scale(intPt1.getT());
      tmp.add(tmp, rayStart);
      intPt1.setIntersectionPoint(tmp);
    }
    return numIntersections;
  }
}
