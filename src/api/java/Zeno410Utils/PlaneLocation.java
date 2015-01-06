package Zeno410Utils;

import net.minecraft.util.ChunkCoordinates;

/**
 *
 * @author Zeno410
 */
public class PlaneLocation {
    public final int x;
    public final int z;

    public PlaneLocation(int _x, int _z) {
        x = _x;
        z = _z;
    }

    public PlaneLocation(ChunkCoordinates coordinates) {
        this(coordinates.posX,coordinates.posZ);
    }

    public float distance(PlaneLocation location) {
        return ((float)(x-location.x))*((float)(x-location.x))+
                ((float)(z-location.z))*((float)(z-location.z));
    }

    public <Type extends Provider> Type closest(Iterable<Type> choices) {
        Type result = null;
        float bestDistance = Float.MAX_VALUE;
        float distance;

        for (Type tested: choices) {
            distance = this.distance(tested.planeLocation());
            if (distance<bestDistance) {
                result = tested;
            }
        }
        return result;
    }

    public abstract class Provider {
        abstract public PlaneLocation planeLocation();
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlaneLocation other = (PlaneLocation) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }
}
