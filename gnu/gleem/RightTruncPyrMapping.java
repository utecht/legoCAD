/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import gnu.gleem.linalg.*;

/** The only mapping gleem supports right now -- a right truncated
    pyramid. */

public class RightTruncPyrMapping implements ScreenToRayMapping {
  public void mapScreenToRay(Vec2f screenCoords,
                             CameraParameters params,
                             Vec3f raySource,
                             Vec3f rayDirection) { 
    Vec3f fwd   = new Vec3f(params.getForwardDirection());
    Vec3f up    = new Vec3f(params.getUpDirection());
    Vec3f right = fwd.cross(up);
    fwd.normalize();
    up.normalize();
    right.normalize();
    float horizFOV = (float) Math.atan(params.imagePlaneAspectRatio * Math.tan(params.vertFOV));
    right.scale((float) (Math.tan(horizFOV)       * screenCoords.get(0)));
    up   .scale((float) (Math.tan(params.vertFOV) * screenCoords.get(1)));
    raySource.set(params.getPosition());
    rayDirection.set(fwd.plus(up).plus(right));
  }
}
