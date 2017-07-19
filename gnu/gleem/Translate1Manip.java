/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.util.*;

import javax.media.opengl.GL;

import gnu.gleem.linalg.*;

/** A Translate1Manip is a Manip which translates in only one
    dimension and whose default representation is a two-way arrow. */

public class Translate1Manip extends Manip {
	private ManipPartTriBased parts;
	private Vec3f translation;
	/** Normalized */
	private Vec3f axis;
	private Vec3f scale;
	/** Local-to-world transform for geometry */
	private Mat4f xform;

	/** Dragging state */
	private Line dragLine;
	/** Dragging state */
	private Vec3f dragOffset;

	public Translate1Manip() {
		parts       = new ManipPartTwoWayArrow();
		translation = new Vec3f(0, 0, 0);
		axis        = new Vec3f(1, 0, 0);
		scale       = new Vec3f(1, 1, 1);
		xform	= new Mat4f();
		dragLine	= new Line();
		dragOffset  = new Vec3f();
		recalc();
	}

	/** Set the translation of this Translate1Manip. This moves its
      on-screen representation. Manipulations cause the translation to
      be modified, not overwritten, so if you want the default
      Translate1Manip to go through the point (0, 1, 0) but still
      translate along the X axis, then setTranslation(0, 1, 0). */
	public void setTranslation(Vec3f translation) {
		this.translation.set(translation);
		recalc();
	}

	/** Get the translation of this Translate1Manip. This corresponds to
      the center of its body. */
	public Vec3f getTranslation() {
		return new Vec3f(translation);
	}

	/** Set the axis of this Translate1Manip. This is the direction
      along which it will travel. Does not need to be normalized, but
      must not be the zero vector. */
	public void setAxis(Vec3f axis) {
		this.axis.set(axis);
		recalc();
	}

	/** Get the axis of this Translate1Manip. */
	public Vec3f getAxis() {
		return new Vec3f(axis);
	}

	/** Set the scale of the Translate1Manip. This only affects the size
      of the on-screen geometry. */
	public void setScale(Vec3f scale) {
		this.scale.set(scale);
		recalc();
	}

	public Vec3f getScale() {
		return new Vec3f(scale);
	}

	/** Change the geometry of this manipulator to be the user-defined
      piece. */
	public void replaceGeometry(ManipPart geom) {
		parts = (ManipPartTriBased)geom;
	}

	public void intersectRay(Vec3f rayStart,
			Vec3f rayDirection,
			List results) {
		parts.intersectRay(rayStart, rayDirection, results, this);
	}

	public void highlight(HitPoint hit) {
		if (hit.manipPart != parts) {
			throw new RuntimeException("My old geometry disappeared; how did this happen?");
		}
		parts.highlight();
	}

	public void clearHighlight() {
		parts.clearHighlight();
	}

	public void makeActive(HitPoint hit) {
		parts.highlight();
		dragLine.setDirection(axis);
		dragLine.setPoint(hit.intPt.getIntersectionPoint());
		dragOffset.sub(translation, hit.intPt.getIntersectionPoint());
	}

	public void drag(Vec3f rayStart,
			Vec3f rayDirection) {
		// Algorithm: Find closest point of ray to dragLine. Add dragOffset
		// to this point to get new translation.
		Vec3f closestPoint = new Vec3f();
		if (dragLine.closestPointToRay(rayStart,
				rayDirection,
				closestPoint) == false) {
			// Drag axis is parallel to ray. Punt.
			return;
		}
		translation.set(closestPoint);
		translation.add(dragOffset);
		recalc();
		super.drag(rayStart, rayDirection);
	}

	public void makeInactive() {
		parts.clearHighlight();
	}

	public void render(GL gl) {
		parts.render(gl);
	}
	public void setColor(Vec3f c)
	{
		parts.setColor(c);
	}
	private void recalc() {
		// Construct local to world transform for geometry.
		// Scale, Rotation, Translation. Since we're right multiplying
		// column vectors, the actual matrix composed is TRS.
		Mat4f scaleMat = new Mat4f();
		Mat4f rotMat   = new Mat4f();
		Mat4f xlateMat = new Mat4f();
		Mat4f tmpMat   = new Mat4f();
		scaleMat.makeIdent();
		scaleMat.set(0, 0, scale.x());
		scaleMat.set(1, 1, scale.y());
		scaleMat.set(2, 2, scale.z());
		// Perpendiculars
		Vec3f p0 = new Vec3f();
		Vec3f p1 = new Vec3f();
		MathUtil.makePerpendicular(axis, p0);
		p1.cross(axis, p0);
		// axis, p0, p1 correspond to x, y, z
		p0.normalize();
		p1.normalize();
		rotMat.makeIdent();
		rotMat.set(0, 0, axis.x());
		rotMat.set(1, 0, axis.y());
		rotMat.set(2, 0, axis.z());
		rotMat.set(0, 1, p0.x());
		rotMat.set(1, 1, p0.y());
		rotMat.set(2, 1, p0.z());
		rotMat.set(0, 2, p1.x());
		rotMat.set(1, 2, p1.y());
		rotMat.set(2, 2, p1.z());
		xlateMat.makeIdent();
		xlateMat.set(0, 3, translation.x());
		xlateMat.set(1, 3, translation.y());
		xlateMat.set(2, 3, translation.z());
		tmpMat.mul(xlateMat, rotMat);
		xform.mul(tmpMat, scaleMat);
		parts.setTransform(xform);
	}
	public void setPickable(boolean b)
	{
		parts.setPickable(b);
	}
}
