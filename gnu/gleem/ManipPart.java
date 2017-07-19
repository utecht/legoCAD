/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import javax.media.opengl.GL;

import gnu.gleem.linalg.*;

/** A ManipPart is a visible or invisible sub-part of a manipulator.
    ManipParts are organized into trees. */

public abstract class ManipPart {
  private ManipPartGroup parent;

  /** Get the parent ManipPartGroup, or null if none (NOTE that this
      differs from the C++ API) */
  public ManipPartGroup getParent() {
    return parent;
  }

  /** Set the parent ManipPartGroup of this ManipPart (NOTE that this
      differs from the C++ API) */
  public void setParent(ManipPartGroup parent) {
    this.parent = parent;
  }

  /** Intersect a ray with this part, returning all intersected points
      as HitPoints in the result list. The same rules as
      Manip.intersectRay() apply. */
  public abstract void intersectRay(Vec3f rayStart,
				    Vec3f rayDirection,
				    List results,
				    Manip caller);

  /** Sets the transform of this part. */
  public abstract void setTransform(Mat4f xform);

  /** Highlight this part */
  public abstract void highlight();

  /** Unhighlight this part */
  public abstract void clearHighlight();

  /** Is this part pickable, or just decorative? Not pickable implies
      that intersectRay() will return immediately. */
  public abstract void setPickable(boolean pickable);
  public abstract boolean getPickable();

  /** Is this part visible? */
  public abstract void setVisible(boolean visible);
  public abstract boolean getVisible();

  /** Render this ManipPart now using the given OpenGL routines and
      assuming an OpenGL context is current. */
  public abstract void render(GL gl);
}
