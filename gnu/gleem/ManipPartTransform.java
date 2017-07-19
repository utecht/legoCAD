/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import gnu.gleem.linalg.*;

/** A Group which contains an offset transformation which is performed
    before the one handed down in setTransform. */

public class ManipPartTransform extends ManipPartGroup {
  private Mat4f offsetTransform;

  public ManipPartTransform() {
    super();
    offsetTransform = new Mat4f();
    offsetTransform.makeIdent();
  }

  public void setTransform(Mat4f xform) {
    Mat4f totalXform = xform.mul(offsetTransform);
    for (int i = 0; i < getNumChildren(); i++) {
      getChild(i).setTransform(totalXform);
    }
  }

  public void setOffsetTransform(Mat4f offsetTransform) {
    this.offsetTransform.set(offsetTransform);
  }

  public Mat4f getOffsetTransform() {
    return offsetTransform;
  }
}
