/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Zeno410Utils;

/**
 * This class is for keying maps of objects to Minecraft locations
 * @author Zeno410
 */
public class BlockLocation {
    private int x;
    private int y;
    private int z;

    public static BlockLocation fetch(int x, int y, int z) {
        // so I can make an object cache if appropriate
        // but not doing it yet
        return new BlockLocation(x,y,z);
    }

    public BlockLocation(int x, int y, int z) {
        this.x = x; this.y = y; this.z = z;
    }

    public final int x() {return x;}
    public final int y() {return y;}
    public final int z() {return z;}

    void setX(int newX) {x = newX;}
    void setY(int newY) {y = newY;}
    void setZ(int newZ) {z = newZ;}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.y;
        hash = 71 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BlockLocation)) {
            return false;
        }
        final BlockLocation other = (BlockLocation) obj;
        if (this.x != other.x()) {
            return false;
        }
        if (this.y != other.y()) {
            return false;
        }
        if (this.z != other.z()) {
            return false;
        }
        return true;
    }
}
