package tinker.common;

public class CoordTuple
{
	public final int x;
	public final int y;
	public final int z;
	
	public CoordTuple(int posX, int posY, int posZ)
	{
		x = posX;
		y = posY;
		z = posZ;
	}
	
	public boolean equals(CoordTuple coord)
	{
		if (coord == null)
			return false;
		else if (coord == this)
			return true;
		else if (coord.x == this.x && coord.y == this.y && coord.z == this.z)
			return true;
		else
			return false;
	}
	
	public boolean equals(int posX, int posY, int posZ)
	{
		if (this.x == posX && this.y == posY && this.z == posZ)
			return true;
		else
			return false;
	}
}
