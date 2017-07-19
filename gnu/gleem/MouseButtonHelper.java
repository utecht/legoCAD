/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.security.*;

/** Helper class for figuring out how many mouse buttons are
    available. (Does not seem to be a way of figuring this out with
    the AWT.) */

public class MouseButtonHelper {
  /** Returns the number of buttons on the mouse device. This is only
      a guess and the implementation may need to be extended to
      support other operating systems (in particular, Mac OS). */
  public static int numMouseButtons() {
    String osName = null;
    if (System.getSecurityManager() != null) {
      osName = (String)
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              return System.getProperty("os.name");
            }
          });
    } else {
      osName = System.getProperty("os.name");
    }
    return mouseButtonsForOS(osName);
  }

  private static int mouseButtonsForOS(String osName) {
    if (osName.startsWith("Windows")) {
      return 2;
    } else {
      // Assume X11 and a three-button mouse
      return 3;
    }
  }
}
