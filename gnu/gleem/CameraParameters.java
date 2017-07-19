/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import gnu.gleem.linalg.*;

/** Container class for camera's parameters. */

public class CameraParameters {
  private Vec3f position         = new Vec3f();
  private Vec3f forwardDirection = new Vec3f();
  private Vec3f upDirection      = new Vec3f();
  float vertFOV;
  float imagePlaneAspectRatio;
  int xSize;
  int ySize;

  public CameraParameters() {}

  public CameraParameters(Vec3f position,
                          Vec3f forwardDirection,
                          Vec3f upDirection,
                          float vertFOV,
                          float imagePlaneAspectRatio,
                          int   xSize,
                          int   ySize) {
    setPosition(position);
    setForwardDirection(forwardDirection);
    setUpDirection(upDirection);
    setVertFOV(vertFOV);
    setImagePlaneAspectRatio(imagePlaneAspectRatio);
    setXSize(xSize);
    setYSize(ySize);
  }

  public void set(CameraParameters params) {
    setPosition(params.getPosition());
    setForwardDirection(params.getForwardDirection());
    setUpDirection(params.getUpDirection());
    setVertFOV(params.getVertFOV());
    setImagePlaneAspectRatio(params.getImagePlaneAspectRatio());
    setXSize(params.getXSize());
    setYSize(params.getYSize());
  }

  /** Sets 3-space origin of camera */
  public void  setPosition(Vec3f position)           { this.position.set(position);   }
  /** Gets 3-space origin of camera */
  public Vec3f getPosition()                         { return position;               }
  /** Sets 3-space forward direction of camera. Does not need to be
      normalized. */
  public void  setForwardDirection(Vec3f fwd)        { forwardDirection.set(fwd);     }
  /** Gets 3-space forward direction of camera. */
  public Vec3f getForwardDirection()                 { return forwardDirection;       }
  /** Sets 3-space upward direction of camera. This must be orthogonal
      to the viewing direction, but does not need to be normalized. */
  public void  setUpDirection(Vec3f up)              { upDirection.set(up);           }
  /** Gets 3-space upward direction of camera. */
  public Vec3f getUpDirection()                      { return upDirection;            }

  /** Takes HALF of the vertical angular span of the frustum,
      specified in radians. For example, if your <b>fovy</b> argument
      to gluPerspective() is 90, then this would be M_PI / 4. */
  public void  setVertFOV(float vertFOV)             { this.vertFOV = vertFOV;        }
  /** Returns HALF of the vertical angular span of the frustum,
      specified in radians. */
  public float getVertFOV()                          { return vertFOV;                }
  /** Sets the aspect ratio of the image plane. Note that this does not
      necessarily have to correspond to the aspect ratio of the
      window. */
  public void  setImagePlaneAspectRatio(float ratio) { imagePlaneAspectRatio = ratio; }
  /** Gets the aspect ratio of the image plane. */
  public float getImagePlaneAspectRatio()            { return imagePlaneAspectRatio;  }

  /** Sets the horizontal size of the window, in pixels */
  public void setXSize(int xSize)                    { this.xSize = xSize;            }
  /** Gets the horizontal size of the window, in pixels */
  public int  getXSize()                             { return xSize;                  }
  /** Sets the vertical size of the window, in pixels */
  public void setYSize(int ySize)                    { this.ySize = ySize;            }
  /** Gets the vertical size of the window, in pixels */
  public int  getYSize()                             { return ySize;                  }


}
