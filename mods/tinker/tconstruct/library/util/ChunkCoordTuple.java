package mods.tinker.tconstruct.library.util;

public class ChunkCoordTuple
{
    public final int x;
    public final int z;

    public ChunkCoordTuple(int posX, int posZ)
    {
        x = posX;
        z = posZ;
    }

    public boolean equalCoords (int posX, int posZ)
    {
        if (this.x == posX && this.z == posZ)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        
        if(getClass() == obj.getClass())
        {
            ChunkCoordTuple coord = (ChunkCoordTuple)obj;
            if(this.x == coord.x && this.z == coord.z)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 37;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }

    public String toString ()
    {
        return "ChunkX: " + x + ", ChunkZ: " + z;
    }
}
