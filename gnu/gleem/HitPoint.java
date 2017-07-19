/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import gnu.gleem.linalg.*;

/** Defines an intersection of a ray with a piece of a manipulator */

public class HitPoint {
  /** Was the shift key down while this HitPoint was computed? */
  public boolean shiftDown;

  /** The manipulator which was intersected */
  public Manip manipulator;

  /** The sub-piece of the manipulator which was actually intersected */
  public ManipPart manipPart;
  
  /** Start of the ray which was cast. The manipulator part must set
      this when an intersection is detected. */
  public Vec3f rayStart;
  /** Direction of the ray which was cast. The manipulator part must
      set this when an intersection is detected. */
  public Vec3f rayDirection;

  // Not all ManipParts supply all of these pieces of information.

  /** The combination of 3D point and t parameter at which the
      intersection occurred. It's important to supply the t parameter
      because the ManipManager needs it to disambiguate between
      intersections with multiple manipulators. */
  public IntersectionPoint intPt;

  /** Arbitrary user data for extended functionality */
  public Object userData;
}
