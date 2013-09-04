/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class UseLogTurnerEvent extends PlayerEvent {

	public final ItemStack	current;
	public final World		world;
	public final int		x;
	public final int		y;
	public final int		z;

	private boolean			handled	= false;

	public UseLogTurnerEvent(EntityPlayer player, ItemStack current,
			World world, int x, int y, int z)
	{
		super(player);
		this.current = current;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean isHandled() {
		return handled;
	}

	public void setHandled() {
		handled = true;
	}
}
