package tconstruct.library.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event;

public class ToolCraftedEvent extends Event {

	public EntityPlayer player;
	public ItemStack tool;
	
	public ToolCraftedEvent(EntityPlayer player, ItemStack tool){
		this.player = player;
		this.tool = tool;
	}
	
}
