/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Utility math routines. */

public class MathUtil {
  /** Makes an arbitrary vector perpendicular to <B>src</B> and
      inserts it into <B>dest</B>. Returns false if the source vector
      was equal to (0, 0, 0). */
  public static boolean makePerpendicular(Vec3f src,
                                          Vec3f dest) {
    if ((src.x() == 0.0f) && (src.y() == 0.0f) && (src.z() == 0.0f)) {
      return false;
    }

    if (src.x() != 0.0f) {
      if (src.y() != 0.0f) {
	dest.set(-src.y(), src.x(), 0.0f);
      }	else {
	dest.set(-src.z(), 0.0f, src.x());
      }
    } else {
      dest.set(1.0f, 0.0f, 0.0f);
    }
    return true;
  }

  /** Returns 1 if the sign of the given argument is positive; -1 if
      negative; 0 if 0. */
  public static int sgn(float f) {
    if (f > 0) {
      return 1;
    } else if (f < 0) {
      return -1;
    }
    return 0;
  }

  /** Clamps argument between min and max values. */
  public static float clamp(float val, float min, float max) {
    if (val < min) return min;
    if (val > max) return max;
    return val;
  }

  /** Clamps argument between min and max values. */
  public static int clamp(int val, int min, int max) {
    if (val < min) return min;
    if (val > max) return max;
    return val;
  }
}
