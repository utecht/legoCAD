/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import javax.media.opengl.GLAutoDrawable;


/** A WindowUpdateListener is used by the ManipManager to transmit
    repaint() notifications to windows containing Manips. When a Manip
    is moved, the ManipManager sends update notifications to all
    GLDrawables in which that Manip is shown. */

public interface WindowUpdateListener {
  public void update(GLAutoDrawable window);
}
