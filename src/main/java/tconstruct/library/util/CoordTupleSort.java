package tconstruct.library.util;

import java.util.Comparator;

public class CoordTupleSort implements Comparator
{
    @Override
    public int compare (Object o1, Object o2)
    {
        CoordTuple c1 = (CoordTuple) o1;
        CoordTuple c2 = (CoordTuple) o2;

        //Sort by y, then x, then z
        if (c1.y != c2.y)
            return c1.y - c2.y;

        if (c1.x != c2.x)
            return c1.x - c2.x;

        if (c1.z != c2.z)
            return c1.z - c2.z;

        return 0;
    }
}
