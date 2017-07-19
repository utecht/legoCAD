/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Arbitrary-length integer vector class. Currently very simple and
    only supports a few needed operations. */

public class Veci {
  private int[] data;

  public Veci(int n) {
    data = new int[n];
  }

  public Veci(Veci arg) {
    data = new int[arg.data.length];
    System.arraycopy(arg.data, 0, data, 0, data.length);
  }

  public int length() {
    return data.length;
  }

  public int get(int i) {
    return data[i];
  }

  public void set(int i, int val) {
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

  public Vecf toVecf() {
    Vecf out = new Vecf(length());
    for (int i = 0; i < length(); i++) {
      out.set(i, get(i));
    }
    return out;
  }
}
