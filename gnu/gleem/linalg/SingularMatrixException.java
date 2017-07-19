/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Thrown to indicate a singular matrix during an inversion or
    related operation. */

public class SingularMatrixException extends RuntimeException {
  public SingularMatrixException() {
    super();
  }

  public SingularMatrixException(String msg) {
    super(msg);
  }
}
