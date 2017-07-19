package gnu.gleem;

import gnu.gleem.HandleBoxManip.FaceInfo;
import gnu.gleem.linalg.IntersectionPoint;
import gnu.gleem.linalg.Line;
import gnu.gleem.linalg.Mat4f;
import gnu.gleem.linalg.MathUtil;
import gnu.gleem.linalg.PlaneUV;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec2f;
import gnu.gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;


public class RotateManip extends Manip
{
	private ManipPart parts;
	private Vec3f translation;
	/** Normalized */
	private Vec3f axis;
	private Vec3f scale;
	/** Local-to-world transform for geometry */
	private Mat4f xform;
	private Rotf rotation;
	private Rotf startRot;
	private PlaneUV rotatePlane;
	private float     startAngle;
	public RotateManip() {
		parts       = new ManipPartRotateOrbit();
		translation = new Vec3f(0, 0, 0);
		axis        = new Vec3f(1, 0, 0);
		scale       = new Vec3f(1, 1, 1);
		xform		= new Mat4f();
		rotatePlane = new PlaneUV();
		rotation = new Rotf();
		startRot = new Rotf();
		recalc();
	}
	/** Set the translation of this RotateManip. This moves its
    on-screen representation. Manipulations cause the translation to
    be modified, not overwritten, so if you want the default
    Translate1Manip to go through the point (0, 1, 0) but still
    translate along the X axis, then setTranslation(0, 1, 0). */
	public void setTranslation(Vec3f translation) {
		this.translation.set(translation);
		recalc();
	}

	/** Get the translation of this RotateManip. This corresponds to
    the center of its body. */
	public Vec3f getTranslation() {
		return new Vec3f(translation);
	}
	/** Set the rotation of this RotateManip. */
	public void setRotation(Rotf rotation) {
		this.rotation.set(rotation);
		recalc();
	}

	/** Get the rotation of this RotateManip. */
	public Rotf getRotation() {
		return new Rotf(rotation);
	}
	/** Set the axis of this RotateManip. This is the direction
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

	/** Set the scale of the RotateManip. This only affects the size
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
		parts = geom;
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
		/*float dotp0 =
			Math.abs(hit.rayDirection.dot(((FaceInfo) parts.get(rotInfo.faceIdx0)).normal));
		float dotp1 =
			Math.abs(hit.rayDirection.dot(((FaceInfo) faces.get(rotInfo.faceIdx1)).normal));
		int faceIdx;
		if (dotp0 > dotp1)
			faceIdx = rotInfo.faceIdx0;
		else
			faceIdx = rotInfo.faceIdx1;
		FaceInfo face = (FaceInfo) faces.get(faceIdx);*/
		// Set up the rotation plane
		rotatePlane.setOrigin(translation);
		//rotatePlane.setNormal(face.normal);
		Vec3f normal = new Vec3f(0, 1, 0);
		rotation.rotateVector(normal);
		//rotatePlane.setNormal(normal);

		Vec3f dummy = new Vec3f();
		Vec2f startUV = new Vec2f();
		rotatePlane.projectPoint(hit.intPt.getIntersectionPoint(), dummy, startUV);
		startAngle = (float) Math.atan2(startUV.y(), startUV.x());
		startRot.set(rotation);

	}

	public void drag(Vec3f rayStart,
			Vec3f rayDirection) {

		IntersectionPoint intPt = new IntersectionPoint();
		Vec2f uvCoords = new Vec2f();
		if (rotatePlane.intersectRay(rayStart,
				rayDirection,
				intPt,
				uvCoords) == false) {
			// Ray is parallel to plane. Punt.
			return;
		}
		// Compute offset rotation angle
		Rotf offsetRot = new Rotf();
		offsetRot.set(rotatePlane.getNormal(),
				(float) Math.atan2(uvCoords.y(), uvCoords.x()) - startAngle);
		rotation.mul(offsetRot, startRot);

		recalc();
		super.drag(rayStart, rayDirection);
	}

	public void makeInactive() {
		parts.clearHighlight();
	}

	public void render(GL gl) {
		parts.render(gl);
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
}
