/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import gnu.gleem.linalg.*;

/** <P> This interface defines the mapping from normalized screen
    coordinates to a 3D ray based on the given camera parameters. You
    could subclass it to allow for more types of camera frusta, but
    would also have to modify the CameraParameters structure. </P>

    <P> The "normalized" screen coordinates must have the following
    properties: </P>
    
    <P>
    upper left corner = (-1, 1) <BR>
    lower left corner = (-1, -1) <BR>
    lower right corner = (1, -1) <BR>
    upper right corner = (1, 1) <BR>
    center = (0, 0)
    </P>

    <P> The application is responsible for specifying the window size
    to allow the ManipManager to compute these coordinates. </P>
*/

public interface ScreenToRayMapping {
  /** Maps screen (x, y) to 3D point source and direction based on
      given CameraParameters. screenCoords and params are incoming
      arguments; raySource and rayDirection are mutated to contain the
      result. */
  public void mapScreenToRay(Vec2f screenCoords,
                             CameraParameters params,
                             Vec3f raySource,
                             Vec3f rayDirection);
}
