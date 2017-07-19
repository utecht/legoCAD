/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem.linalg;

/** Thrown to indicate a non-square matrix during an operation
    requiring one. */

public class NonSquareMatrixException extends RuntimeException {
  public NonSquareMatrixException() {
    super();
  }

  public NonSquareMatrixException(String msg) {
    super(msg);
  }
}
