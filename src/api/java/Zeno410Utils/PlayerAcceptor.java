
package Zeno410Utils;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 * @author Zeno410
 */
public abstract class PlayerAcceptor<Type> {
    abstract public void accept(EntityPlayer player,Type data);
}
