/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.awt.event.*;
import java.util.*;

import javax.media.opengl.*;

import gnu.gleem.linalg.*;


/** The ManipManager handles making manipulators visible in a
    window. */

public class ManipManager {
  // Screen-to-ray mapping
  private ScreenToRayMapping mapping;
  private static ManipManager soleInstance;
  // Maps GLDrawables to WindowInfos
  private Map windowToInfoMap = new HashMap();
  // Maps Manips to Set<GLDrawable>
  private Map manipToWindowMap = new HashMap();
  // MouseAdapter for this
  private MouseAdapter mouseListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        ManipManager.this.mousePressed(e);
      }

      public void mouseReleased(MouseEvent e) {
        ManipManager.this.mouseReleased(e);
      }
    };
  private MouseMotionAdapter mouseMotionListener = new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        ManipManager.this.mouseDragged(e);
      }

      public void mouseMoved(MouseEvent e) {
        ManipManager.this.mouseMoved(e);
      }
    };
  private WindowUpdateListener defaultWindowListener = new WindowUpdateListener() {
      public void update(GLAutoDrawable window) {
        //if (!(window instanceof GLRunnable)) {
          window.repaint();
       // }
      }
    };
  private WindowUpdateListener windowListener;

  class WindowInfo {
    /** Set<Manip> */
    Set manips = new HashSet();
    CameraParameters params = new CameraParameters();
    Manip curHighlightedManip;
    // Current manip
    Manip curManip;
    // Dragging?
    boolean dragging;
  }

  /** This class is a singleton. Get the sole instance of the
      ManipManager. */
  public static synchronized ManipManager getManipManager() {
    if (soleInstance == null) {
      soleInstance = new ManipManager();
    }
    return soleInstance;
  }

  /** Make the ManipManager aware of the existence of a given
      window. This causes mouse and mouse motion listeners to be
      installed on this window; see setupMouseListeners, below. */
  public synchronized void registerWindow(GLAutoDrawable window) {
    windowToInfoMap.put(window, new WindowInfo());
    setupMouseListeners(window);
  }

  /** Remove all references to a given window, including removing all
      manipulators from it. */
  public synchronized void unregisterWindow(GLAutoDrawable window) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    for (Iterator iter = info.manips.iterator(); iter.hasNext(); ) {
      removeManipFromWindow((Manip) iter.next(), window);
    }
    windowToInfoMap.remove(window);
    removeMouseListeners(window);
  }

  /** Make a given manipulator visible and active in a given
      window. The window must be registered. */
  public synchronized void showManipInWindow(Manip manip, GLDrawable window) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    if (info == null) {
      throw new RuntimeException("Window not registered");
    }
    info.manips.add(manip);
    Set windows = (Set) manipToWindowMap.get(manip);
    if (windows == null) {
      windows = new HashSet();
      manipToWindowMap.put(manip, windows);
    }
    windows.add(window);
  }

  /** Remove a given manipulator from a given window. The window must
      be registered. */
  public synchronized void removeManipFromWindow(Manip manip, GLDrawable window) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    if (info == null) {
      throw new RuntimeException("Window not registered");
    }
    if (info.manips.remove(manip)) {
      //throw new RuntimeException("Manip not registered in window");
    	Set windows = (Set) manipToWindowMap.get(manip);
        assert windows != null;
        windows.remove(window);
    }

  }

  /** This must be called for a registered window every time the
      camera parameters of the window change. */
  public synchronized void updateCameraParameters(GLDrawable window, CameraParameters params) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    if (info == null) {
      throw new RuntimeException("Window not registered");
    }
    info.params.set(params);
  }

  /** Allows changing of the screen-to-ray mapping. Default is a
      RightTruncPyrMapping. */
  public synchronized void setScreenToRayMapping(ScreenToRayMapping mapping) {
    this.mapping = mapping;
  }

  /** Returns the current screen-to-ray mapping. */
  public synchronized ScreenToRayMapping getScreenToRayMapping() {
    return mapping;
  }

  /** Sets the WindowUpdateListener the ManipManager uses to force
      repainting of windows in which manipulators have moved. The
      default implementation, which can be restored by passing a null
      listener argument to this method, calls repaint() on the
      GLDrawable if it is not a GLRunnable instance (i.e., a
      GLAnimCanvas or GLAnimJPanel, which redraw themselves
      automatically). */
  public synchronized void setWindowUpdateListener(WindowUpdateListener listener) {
    if (listener != null) {
      windowListener = listener;
    } else {
      windowListener = defaultWindowListener;
    }
  }

  /** Cause the manipulators for a given window to be drawn. The
      drawing occurs immediately; this routine must be called when an
      OpenGL context is valid, i.e., from within the display() method
      of a GLEventListener. */
  public synchronized void render(GLDrawable window, GL gl) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    if (info == null) {
      throw new RuntimeException("Window not registered");
    }
    for (Iterator iter = info.manips.iterator(); iter.hasNext(); ) {
      ((Manip) iter.next()).render(gl);
    }
  }

  /** Sets up a MouseListener and MouseMotionListener for the given
      window. Since an application-level MouseListener or
      MouseMotionListener might want to intercept events and not pass
      them on to the ManipManager without relying on the ordering of
      listeners for the canvas (see the ExaminerViewer class), the
      setupMouseListeners and removeMouseListeners routines, as well
      as the appropriate delegate routines, are made public here. */
  public synchronized void setupMouseListeners(GLAutoDrawable window) {
    window.addMouseMotionListener(mouseMotionListener);
    window.addMouseListener(mouseListener);
  }

  /** Removes the automatically-installed mouse listeners for the
      given window. This allows application code to determine the
      policy for intercepting mouse events. */
  public synchronized void removeMouseListeners(GLAutoDrawable window) {
    window.removeMouseMotionListener(mouseMotionListener);
    window.removeMouseListener(mouseListener);
  }

  /** The ManipManager watches for the following events: mouseMoved,
      mouseDragged, mousePressed, and mouseReleased. These routines
      are exposed so application-level code can intercept events when
      certain modifier keys are depressed. */
  public synchronized void mouseMoved(MouseEvent e) {
    passiveMotionMethod((GLDrawable) e.getComponent(), e.getX(), e.getY());
  }

  /** The ManipManager watches for the following events: mouseMoved,
      mouseDragged, mousePressed, and mouseReleased. These routines
      are exposed so application-level code can intercept events when
      certain modifier keys are depressed. */
  public synchronized void mouseDragged(MouseEvent e) {
    motionMethod((GLDrawable) e.getComponent(), e.getX(), e.getY());
  }

  /** The ManipManager watches for the following events: mouseMoved,
      mouseDragged, mousePressed, and mouseReleased. These routines
      are exposed so application-level code can intercept events when
      certain modifier keys are depressed. */
  public synchronized void mousePressed(MouseEvent e) {
    mouseMethod((GLDrawable) e.getComponent(), e.getModifiers(),
                true, e.getX(), e.getY());
  }

  /** The ManipManager watches for the following events: mouseMoved,
      mouseDragged, mousePressed, and mouseReleased. These routines
      are exposed so application-level code can intercept events when
      certain modifier keys are depressed. */
  public synchronized void mouseReleased(MouseEvent e) {
    mouseMethod((GLDrawable) e.getComponent(), e.getModifiers(),
                false, e.getX(), e.getY());
  }

  //----------------------------------------------------------------------
  // Internals only below this point
  //

  private ManipManager() {
    mapping = new RightTruncPyrMapping();
    setWindowUpdateListener(null);
  }

  private void motionMethod(GLDrawable window, int x, int y) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    if (info.dragging) {
      // Compute ray in 3D
      Vec3f rayStart     = new Vec3f();
      Vec3f rayDirection = new Vec3f();
      computeRay(info.params, x, y, rayStart, rayDirection);
      info.curManip.drag(rayStart, rayDirection);
      fireUpdate(info.curManip);
    }
  }

  private void passiveMotionMethod(GLDrawable window, int x, int y) {
    WindowInfo info = (WindowInfo) windowToInfoMap.get(window);
    // Compute ray in 3D
    Vec3f rayStart     = new Vec3f();
    Vec3f rayDirection = new Vec3f();
    computeRay(info.params, x, y, rayStart, rayDirection);
    // Compute all hits
    List hits = new ArrayList();
    for (Iterator iter = info.manips.iterator(); iter.hasNext(); ) {
      ((Manip) iter.next()).intersectRay(rayStart, rayDirection, hits);
    }
    // Find closest one
    HitPoint hp = null;
    for (Iterator iter = hits.iterator(); iter.hasNext(); ) {
      HitPoint cur = (HitPoint) iter.next();
      if ((hp == null) || (cur.intPt.getT() < hp.intPt.getT())) {
        hp = cur;
      }
    }
    if (info.curHighlightedManip != null) {
      info.curHighlightedManip.clearHighlight();
      fireUpdate(info.curHighlightedManip);
    }
    if (hp != null) {
      // Highlight manip
      info.curHighlightedManip = hp.manipulator;
      info.curHighlightedManip.highlight(hp);
      fireUpdate(info.curHighlightedManip);
    } else {
      info.curHighlightedManip = null;
    }
  }

  private void mouseMethod(GLDrawable window, int modifiers,
                           boolean isPress, int x, int y) {
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
      WindowInfo info = (WindowInfo) windowToInfoMap.get(window);

      if (isPress) {
        // Compute ray in 3D
        Vec3f rayStart     = new Vec3f();
        Vec3f rayDirection = new Vec3f();
        computeRay(info.params, x, y, rayStart, rayDirection);
        // Compute all hits
        List hits = new ArrayList();
        for (Iterator iter = info.manips.iterator(); iter.hasNext(); ) {
          ((Manip) iter.next()).intersectRay(rayStart, rayDirection, hits);
        }
        // Find closest one
        HitPoint hp = null;
        for (Iterator iter = hits.iterator(); iter.hasNext(); ) {
          HitPoint cur = (HitPoint) iter.next();
          if ((hp == null) || (cur.intPt.getT() < hp.intPt.getT())) {
            hp = cur;
          }
        }
        if (hp != null) {
          if (info.curHighlightedManip != null) {
            info.curHighlightedManip.clearHighlight();
            fireUpdate(info.curHighlightedManip);
            info.curHighlightedManip = null;
          }

          if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            hp.shiftDown = true;
          }

          hp.manipulator.makeActive(hp);
          info.curManip = hp.manipulator;
          info.dragging = true;
          fireUpdate(info.curManip);
        }
      } else {
        if (info.curManip != null) {
          info.curManip.makeInactive();
          info.dragging = false;
          fireUpdate(info.curManip);
          info.curManip = null;
          // Check to see where mouse is
          passiveMotionMethod(window, x, y);
        }
      }
    }
  }

  private Vec2f screenToNormalizedCoordinates(CameraParameters params,
                                              int x, int y) {
    // AWT's origin is upper left
    return new Vec2f(
      (((float) x / (float) (params.xSize - 1)) - 0.5f) * 2.0f,
      (0.5f - ((float) y / (float) (params.ySize - 1))) * 2.0f
    );
  }

  private void computeRay(CameraParameters params, int x, int y,
                          Vec3f raySource, Vec3f rayDirection) {
    if (mapping == null) {
      throw new RuntimeException("Screen to ray mapping was unspecified");
    }
    mapping.mapScreenToRay(screenToNormalizedCoordinates(params, x, y),
                           params,
                           raySource,
                           rayDirection);
  }

  private void fireUpdate(Manip manip) {
    Set windows = (Set) manipToWindowMap.get(manip);
    assert windows != null;
    for (Iterator iter = windows.iterator(); iter.hasNext(); ) {
      windowListener.update((GLAutoDrawable) iter.next());
    }
  }
}
