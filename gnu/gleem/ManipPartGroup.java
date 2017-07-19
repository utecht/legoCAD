/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import javax.media.opengl.GL;

import gnu.gleem.linalg.*;

/** This class groups a set of ManipParts. Makes a set of ManipParts
    look like one. */

public class ManipPartGroup extends ManipPart {
  private boolean pickable = true;
  private boolean visible = true;
  private List children = new ArrayList();

  public void addChild(ManipPart child) {
    children.add(child);
  }

  public void removeChild(ManipPart child) {
    children.remove(child);
  }

  public int getNumChildren() {
    return children.size();
  }

  public ManipPart getChild(int index) {
    return (ManipPart) children.get(index);
  }

  public void intersectRay(Vec3f rayStart,
                           Vec3f rayDirection,
                           List results,
                           Manip caller) {
    if (!pickable) {
      return;
    }

    int topIdx = results.size();
    for (int i = 0; i < getNumChildren(); i++) {
      getChild(i).intersectRay(rayStart, rayDirection, results, caller);
    }

    // Fix up all HitPoints so we appear to be the manipulator part
    // which caused the intersection
    for (int i = topIdx; i < results.size(); i++) {
      ((HitPoint) results.get(i)).manipPart = this;
    }
  }

  public void setTransform(Mat4f xform) {
    for (int i = 0; i < getNumChildren(); i++) {
      getChild(i).setTransform(xform);
    }
  }

  public void highlight() {
    for (int i = 0; i < getNumChildren(); i++) {
      getChild(i).highlight();
    }
  }

  public void clearHighlight() {
    for (int i = 0; i < getNumChildren(); i++) {
      getChild(i).clearHighlight();
    }
  }

  public void setPickable(boolean pickable) {
    this.pickable = pickable;
  }

  public boolean getPickable() {
    return pickable;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
    for (Iterator iter = children.iterator(); iter.hasNext(); ) {
      ((ManipPart) iter.next()).setVisible(visible);
    }
  }

  public boolean getVisible() {
    return visible;
  }

  public void render(GL gl) {
    for (Iterator iter = children.iterator(); iter.hasNext(); ) {
      ((ManipPart) iter.next()).render(gl);
    }
  }
}
