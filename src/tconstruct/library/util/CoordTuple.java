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
        if (coord.equals(this))
            return 0;
        
        if (coord.y != y)
            return y - coord.y;
        
        if (coord.x != x)
            return x - coord.x;
        
        if (coord.z != z)
            return z - coord.z;
        
        return 0;
    }
}
