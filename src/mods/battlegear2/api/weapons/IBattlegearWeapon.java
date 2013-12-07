package mods.battlegear2.api.weapons;

import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandDual;
import mods.battlegear2.api.ISheathed;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IBattlegearWeapon extends ISheathed,IOffhandDual,IAllowItem{

}