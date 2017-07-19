/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import javax.media.opengl.GL;

import gnu.gleem.linalg.*;

/** A line segment from (-1, 0, 0) to (1, 0, 0). */

public class ManipPartLineSeg extends ManipPart {
  private Vec3f color;
  private Vec3f highlightColor;
  private boolean highlighted;
  private boolean visible;
  private static Vec3f[] vertices = new Vec3f[] {
    new Vec3f(-1, 0, 0),
    new Vec3f(1, 0, 0)
  };
  /** Current transformation matrix */
  private Mat4f xform;
  /** Transformed vertices */
  private Vec3f[] curVertices;

  public ManipPartLineSeg() {
    color          = new Vec3f(0.8f, 0.8f, 0.8f);
    highlightColor = new Vec3f(0.8f, 0.8f, 0.2f);
    highlighted    = false;
    visible	   = true;
    xform          = new Mat4f();
    xform.makeIdent();
    curVertices    = null;
  }

  /** Default color is (0.8, 0.8, 0.8) */
  public void setColor(Vec3f color) {
    this.color.set(color);
  }

  public Vec3f getColor() {
    return new Vec3f(color);
  }

  /** Default highlight color is (0.8, 0.8, 0) */
  public void setHighlightColor(Vec3f highlightColor) {
    this.highlightColor.set(highlightColor);
  }

  public Vec3f getHighlightColor() {
    return new Vec3f(highlightColor);
  }

  public void intersectRay(Vec3f rayStart,
			   Vec3f rayDirection,
			   List results,
			   Manip caller) {
  }

  public void setTransform(Mat4f xform) {
    this.xform.set(xform);
    recalcVertices();
  }

  public void highlight() {
    highlighted = true;
  }

  public void clearHighlight() {
    highlighted = false;
  }

  public void setPickable(boolean pickable) {
  }

  public boolean getPickable() {
    return false;
  }

  /** Default is visible */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean getVisible() {
    return visible;
  }

  public void render(GL gl) {
    if (!visible)
      return;
    // FIXME: probably too slow
    boolean reenable = gl.glIsEnabled(GL.GL_LIGHTING);
    gl.glDisable(GL.GL_LIGHTING);
    gl.glBegin(GL.GL_LINES);
    if (highlighted)
      gl.glColor3f(highlightColor.x(), highlightColor.y(), highlightColor.z());
    else
      gl.glColor3f(color.x(), color.y(), color.z());
    for (int i = 0; i < curVertices.length; i++) {
      Vec3f v = curVertices[i];
      gl.glVertex3f(v.x(), v.y(), v.z());
    }
    gl.glEnd();
    if (reenable)
      gl.glEnable(GL.GL_LIGHTING);
  }

  //----------------------------------------------------------------------
  // Internals only below this point
  //

  private void recalcVertices() {
    if ((curVertices == null) ||
        (curVertices.length != vertices.length)) {
      curVertices = new Vec3f[vertices.length];
      for (int i = 0; i < vertices.length; i++) {
        curVertices[i] = new Vec3f();
      }
    }

    for (int i = 0; i < curVertices.length; i++) {
      xform.xformPt(vertices[i], curVertices[i]);
    }
  }
}
