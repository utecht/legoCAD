/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import javax.media.opengl.GL;

import gnu.gleem.linalg.*;

/** The base class for all manipulators. Fundamentally a manipulator
    must support a ray cast operation with itself and logic to
    understand what to do when that ray cast actually made the
    manipulator active. */

public abstract class Manip {
  private List motionListeners;

  public Manip() {
    motionListeners = new LinkedList();

    // FIXME: The ManipManager's list should probably be maintained
    // with weak references
    //    ManipManager.getManipManager().registerManip(this);
  }

  /** Returns true if the addition was successful, false otherwise */
  public boolean    addMotionListener(ManipMotionListener l) {
    return motionListeners.add(l);
  }

  /** Returns true if the removal was successful, false otherwise */
  public boolean removeMotionListener(ManipMotionListener l) {
    return motionListeners.remove(l);
  }

  /** Cast a ray in 3-space from the camera start position in the
      specified direction and test for intersections against all live
      portions of this manipulator. Add all hits, in arbitrary order,
      to the end of the given list in the form of HitPoints. Must not
      modify the results vector in any other way (i.e., must not
      remove any existing HitPoints from the results vector). */
  public abstract void intersectRay(Vec3f rayStart,
				    Vec3f rayDirection,
				    List results);

  /** Tell the manipulator to highlight the current portion of itself.
      This is merely visual feedback to the user. */
  public abstract void highlight(HitPoint hit);

  /** Tell the manipulator to clear the current highlight */
  public abstract void clearHighlight();

  /** If the ManipManager decides that this manipulator is to become
      active, it will pass back the HitPoint which made it make its
      decision. The manipulator can then change its state to look for
      drags of this portion of the manipulator. */
  public abstract void makeActive(HitPoint hit);

  /** When a manipulator is active, drags of the live portion cause
      motion of the manipulator. The ManipManager keeps track of which
      manipulator (if any) is active and takes care of calling the
      drag() method with the current ray start and direction. The
      manipulator must keep enough state to understand how it should
      position and/or rotate itself. NOTE that the base class provides
      an implementation for this method which you must call at the end
      of your overriding method. */
  public void drag(Vec3f rayStart,
		   Vec3f rayDirection) {
    for (Iterator iter = motionListeners.iterator(); iter.hasNext(); ) {
      ManipMotionListener m = (ManipMotionListener) iter.next();
      m.manipMoved(this);
    }
  }

  /** When the mouse button is released, makeInactive() is called. The
      manipulator should reset its state in preparation for the next
      drag. */
  public abstract void makeInactive();

  /** Render this Manipulator now using the given OpenGL routines and
      assuming an OpenGL context is current. */
  public abstract void render(GL gl);
}
