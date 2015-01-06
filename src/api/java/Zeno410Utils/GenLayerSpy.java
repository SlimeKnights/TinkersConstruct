package Zeno410Utils;

import net.minecraft.world.gen.layer.*;
import java.util.logging.Logger;

import java.lang.reflect.*;


/**
 *
 * @author Zeno410
 */
public class GenLayerSpy extends GenLayer {
    private final GenLayer spiedUpon;
    private static Logger logger = new Zeno410Logger("GenLayerSpy").logger();
    private static int lines = 0;
    public GenLayerSpy(GenLayer toSpyOn) {
        super(0);
        spiedUpon = toSpyOn;
            Field [] fields = this.getClass().getSuperclass().getDeclaredFields();
            Field parentField = null;
            //logger.info( "fieldcount "+fields.length);
            for (int i = 0; i < fields.length;i ++) {
                //logger.info( fields[i].getName());
                if (fields[i].getName().contains("field_75909_a")) {
                    parentField = fields[i];
                    parentField.setAccessible(true);
                }
            }
            try {
                GenLayer oldParent = (GenLayer)(parentField.get(spiedUpon));
                if (oldParent != null) {
                    GenLayer newParent = new GenLayerSpy(oldParent);
                    parentField.set(spiedUpon,newParent);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
    }
    public int[] getInts(int par1, int par2, int par3, int par4) {
        if (lines < 1000) {
            //logger.info(spiedUpon.getClass().getName() + " " + par1 + " " + par2 + " " + par3 + " " + par4);
            lines ++;
        }
        return spiedUpon.getInts(par1, par2, par3, par4);
    }

}
