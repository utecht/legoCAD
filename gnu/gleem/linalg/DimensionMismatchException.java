/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Thrown to indicate a mismatch of dimensionality of a matrix or
    vector. */

public class DimensionMismatchException extends RuntimeException {
  public DimensionMismatchException() {
    super();
  }

  public DimensionMismatchException(String msg) {
    super(msg);
  }
}
