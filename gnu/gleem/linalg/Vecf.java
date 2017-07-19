/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Arbitrary-length single-precision vector class. Currently very
    simple and only supports a few needed operations. */

public class Vecf {
  private float[] data;

  public Vecf(int n) {
    data = new float[n];
  }

  public Vecf(Vecf arg) {
    data = new float[arg.data.length];
    System.arraycopy(arg.data, 0, data, 0, data.length);
  }

  public int length() {
    return data.length;
  }

  public float get(int i) {
    return data[i];
  }

  public void set(int i, float val) {
    data[i] = val;
  }

  public Vec2f toVec2f() throws DimensionMismatchException {
    if (length() != 2)
      throw new DimensionMismatchException();
    Vec2f out = new Vec2f();
    for (int i = 0; i < 2; i++) {
      out.set(i, get(i));
    }
    return out;
  }

  public Vec3f toVec3f() throws DimensionMismatchException {
    if (length() != 3)
      throw new DimensionMismatchException();
    Vec3f out = new Vec3f();
    for (int i = 0; i < 3; i++) {
      out.set(i, get(i));
    }
    return out;
  }

  public Veci toInt() {
    Veci out = new Veci(length());
    for (int i = 0; i < length(); i++) {
      out.set(i, (int) get(i));
    }
    return out;
  }
}
