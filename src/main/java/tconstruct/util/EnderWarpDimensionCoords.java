package tconstruct.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import tconstruct.library.util.CoordTuple;

public class EnderWarpDimensionCoords
{
    private HashMap<Integer, LinkedList<CoordTuple>> chunkpoords = new HashMap<Integer, LinkedList<CoordTuple>>();

    public CoordTuple getClosestPortal (CoordTuple tuple, int frequency, int rangelimit)
    {
        int distance = rangelimit;
        CoordTuple closest = null;
        LinkedList<CoordTuple> coordList = chunkpoords.get(frequency);

        for (CoordTuple coord : coordList)
        {
            int dx = coord.getDistanceFrom(tuple);
            if (dx < distance && dx != 0)
            {
                distance = dx;
                closest = coord;
            }
        }
        return closest;
    }

    public void registerCoord (CoordTuple coord, int frequency)
    {
        LinkedList<CoordTuple> coordList = chunkpoords.get(frequency);
        if (coordList == null)
        {
            coordList = new LinkedList<CoordTuple>();
            chunkpoords.put(frequency, coordList);
        }
        coordList.add(coord);
    }

    public void removeCoord (CoordTuple coord, int frequency)
    {
        LinkedList<CoordTuple> coordList = chunkpoords.get(frequency);
        if (coordList != null)
        {
            Iterator<CoordTuple> iter = coordList.iterator();

            while (iter.hasNext())
            {
                CoordTuple remove = iter.next();
                if (remove.equals(coord))
                    iter.remove();
            }
        }
    }

    public void changeFrequency (CoordTuple coord, int newFrequency, int prevFrequency)
    {
        registerCoord(coord, newFrequency);
        removeCoord(coord, prevFrequency);
    }
}