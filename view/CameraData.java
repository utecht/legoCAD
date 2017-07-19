package view;

import model.Point3;
import gnu.gleem.ExaminerViewer;
import gnu.gleem.MouseButtonHelper;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;


public class CameraData
{
	private ExaminerViewer view;
	private Rotf initRotation = null;
	private Vec3f initPosition = null;
	private boolean orthogonal = false;

	private boolean displayXManip = true;
	private boolean displayYManip = true;
	private boolean displayZManip = true;

	public CameraData(boolean ortho, Point3 pos, Rotf rot)
	{
		setPosition(pos);

		setRotation(rot);

		orthogonal = ortho;
		view = new ExaminerViewer(MouseButtonHelper.numMouseButtons());

	}
	public void setDisplayX(boolean b)
	{
		displayXManip = b;
	}
	public void setDisplayY(boolean b)
	{
		displayYManip = b;
	}
	public void setDisplayZ(boolean b)
	{
		displayZManip = b;
	}
	public boolean getDisplayX()
	{
		return displayXManip;
	}
	public boolean getDisplayY()
	{
		return displayYManip;
	}
	public boolean getDisplayZ()
	{
		return displayZManip;
	}
	public boolean isOrthogonal()
	{
		return orthogonal;
	}

	public Rotf getRotation()
	{
		return new Rotf(view.getRotation());
	}
	public Point3 getPosition()
	{
		Vec3f pos = view.getPosition();
		return new Point3((float)pos.x(), (float)pos.y(), (float)pos.z());
	}
	public void setRotation(Rotf rot)
	{
		if(!orthogonal)
		{
			if(initRotation == null)
			{
				initRotation = rot;
			} else
			{
				view.setRotation(rot);
			}

		}
	}
	public void setPosition(Point3 loc)
	{
		if(initPosition == null)
		{
			initPosition = new Vec3f((float)loc.x(), (float)loc.y(), (float)loc.z());
		} else
		{
			view.setPosition(new Vec3f((float)loc.x(), (float)loc.y(), (float)loc.z()));
		}
		//view.getPosition().set(new Vec3f((float)loc.x(), (float)loc.y(), (float)loc.z()));
	}
	ExaminerViewer getView()
	{
		return view;
	}
	Rotf getInitRotation()
	{
		return initRotation;
	}
	Vec3f getInitPosition()
	{
		return initPosition;
	}

}
