package tconstruct.library.util;

public class CoordTuple implements Comparable
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

    public CoordTuple(double posX, double posY, double posZ)
    {
        x = (int) Math.floor(posX);
        y = (int) Math.floor(posY);
        z = (int) Math.floor(posZ);
    }

    public CoordTuple(CoordTuple tuple)
    {
        x = tuple.x;
        y = tuple.y;
        z = tuple.z;
    }
    
    public int getDistanceFrom(CoordTuple tuple)
    {
        return getDistanceFrom(tuple.x, tuple.y, tuple.z);
    }
    
    public int getDistanceFrom (int x1, int y1, int z1)
    {
        int xPos = x - x1;
        int yPos = y - y1;
        int zPos = z - z1;
        return xPos * xPos + yPos * yPos + zPos * zPos;
    }

    public boolean equalCoords (int posX, int posY, int posZ)
    {
        if (this.x == posX && this.y == posY && this.z == posZ)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == null)
            return false;

        if (getClass() == obj.getClass())
        {
            CoordTuple coord = (CoordTuple) obj;
            if (this.x == coord.x && this.y == coord.y && this.z == coord.z)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public String toString ()
    {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    @Override
    public int compareTo (Object o)
    {
        if (o == null)
            throw new NullPointerException("Object cannot be null");

        CoordTuple coord = (CoordTuple) o;

        if (x < coord.x)
        {
            return -1;
        }
        if (x > coord.x)
        {
            return 1;
        }
        if (y < coord.y)
        {
            return -1;
        }
        if (y > coord.y)
        {
            return 1;
        }
        if (z < coord.z)
        {
            return -1;
        }
        if (z > coord.z)
        {
            return 1;
        }

        return 0;
    }
}
