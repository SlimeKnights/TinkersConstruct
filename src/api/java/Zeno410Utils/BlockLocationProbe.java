
package Zeno410Utils;

/**
 * This is a class for probing hash sets of BlockLocatable for having blocks
 * it differs in that coordinates can be changed
 * primarily for probing Hashsets
 * @author Zeno410
 */
public class BlockLocationProbe extends BlockLocation{
    public BlockLocationProbe(int x, int y, int z) {
        super(x,y,z);
    }

    public void setX(int newX) {super.setX(newX);}
    public void setY(int newY) {super.setY(newY);}
    public void setZ(int newZ) {super.setZ(newZ);}

    public BlockLocation forStorage() {return new BlockLocation(x(),y(),z());}

}
