package com.waleed.Spaceoids.entities;

import com.waleed.Spaceoids.main.Spaceoids;

public class SpaceObject {

	public float x, y, dx, dy, radians, speed, rotationSpeed, newX, newY, oldX, oldY;

	public boolean death = true;

	protected boolean special;

	protected int width;
	protected int height;

	public float[] shapex;
	protected float[] shapey;

	public float getX() { return x; }
	public float getY() { return y; }

	public float[] getShapex() { return shapex; }
	public float[] getShapey() { return shapey; }
	public boolean getSpecialMove() { return special; }

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}


	public boolean intersects(SpaceObject other) {
		float[] sx = other.getShapex();
		float[] sy = other.getShapey();
		
		for(int i = 0; i < sx.length; i++)
		{
			for(int j = 0; j < sy.length; j++)
			{
				if(contains(sx[i], sy[j]))
					return true;
			}
		}
		return false;
	}

	public boolean contains(float x, float y) {
		boolean contains = false;
		for(int i = 0, j = shapex.length - 1;
				i < shapex.length;
				j = i++) {
			if((shapey[i] > y) != (shapey[j] > y) &&
					(x < (shapex[j] - shapex[i]) *
							(y - shapey[i]) / (shapey[j] - shapey[i])
							+ shapex[i])) {
				contains = !contains;
			}
		}
		return contains;
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
	public void dispose() {
		this.dispose();
	}
	
	

}


















