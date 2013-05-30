package mods.tinker.tconstruct.util.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;

public class TPlayerStats
{
	public WeakReference<EntityPlayer> player;
	public int level;
	public int levelHealth;
	public int bonusHealth;
	public int hunger;
	public boolean beginnerManual;
	public boolean materialManual;
	public boolean smelteryManual;
	public ArmorExtended armor;
}
