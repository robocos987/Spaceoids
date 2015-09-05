package com.waleed.Spaceoids.entities;

import com.waleed.Spaceoids.main.Spaceoids;

public class SpaceObject {

	public float x;
	public float y;

	public float dx;
	public float dy;

	public boolean death = true;

	public float radians;
	public float speed;
	public float rotationSpeed;

	protected boolean special;

	protected int width;
	protected int height;

	protected float[] shapex;
	protected float[] shapey;

	public float getX() { return x; }
	public float getY() { return y; }

	public float[] getShapex() { return shapex; }
	public float[] getShapey() { return shapey; }
	public boolean specialMove() { return special; }

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}


	public boolean intersects(SpaceObject other) {
		float[] sx = other.getShapex();
		float[] sy = other.getShapey();
		for(int i = 0; i < sx.length; i++) {
			if(contains(sx[i], sy[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(float x, float y) {
		boolean b = false;
		for(int i = 0, j = shapex.length - 1;
				i < shapex.length;
				j = i++) {
			if((shapey[i] > y) != (shapey[j] > y) &&
					(x < (shapex[j] - shapex[i]) *
							(y - shapey[i]) / (shapey[j] - shapey[i])
							+ shapex[i])) {
				b = !b;
			}
		}
		return b;
	}

	protected void wrap() {
		if(this instanceof Player)
		{
			if(x < 0)
			{
				x = Spaceoids.WIDTH;
				death = true;
			}
			else if(x > Spaceoids.WIDTH) 
			{
				x = 0;
				death = true;
			}
			else if(y < 0)
			{
				y = Spaceoids.HEIGHT;
				death = true;
			}

			else if(y > Spaceoids.HEIGHT) 
			{
				y = 0;
				death = true;
			}else
			{
				death = false;
			}

		}else
		{
			death = false;
			if(x < 0) x = Spaceoids.WIDTH;
			if(x > Spaceoids.WIDTH) x = 0;
			if(y < 0) y = Spaceoids.HEIGHT;
			if(y > Spaceoids.HEIGHT) y = 0; 
		}
	}

	public boolean deathWrap()
	{

		return death;
	}

}


















