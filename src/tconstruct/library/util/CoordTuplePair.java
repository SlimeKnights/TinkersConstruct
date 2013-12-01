package tconstruct.library.util;

public class CoordTuplePair
{
    public CoordTuple a;
    public CoordTuple b;

    public CoordTuplePair(CoordTuple a, CoordTuple b)
    {
        this.a = a;
        this.b = b;
    }

    public CoordTuplePair(int aX, int aY, int aZ, int bX, int bY, int bZ)
    {
        this.a = new CoordTuple(aX, aY, aZ);
        this.b = new CoordTuple(bX, bY, bZ);
    }
}