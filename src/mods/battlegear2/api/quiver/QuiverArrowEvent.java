package mods.battlegear2.api.quiver;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class QuiverArrowEvent extends PlayerEventChild{

	public int bowDamage = 1;
	public float bowSoundVolume = 1.0F;
	public boolean addEnchantments = true;
	public ArrowLooseEvent event;

	public QuiverArrowEvent(ArrowLooseEvent parent) {
		super(parent);
		this.event = parent;
	}

	public ItemStack getBow()
	{
		return event.bow;
	}
	
	public int getCharge()
	{
		return event.charge;
	}
}
