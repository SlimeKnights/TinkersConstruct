package mods.tinker.tconstruct.items.armor;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.items.CraftingItem;
import mods.tinker.tconstruct.util.player.ArmorExtended;
import mods.tinker.tconstruct.util.player.TPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HeartCanister extends CraftingItem
{

	public HeartCanister(int id)
	{
		super(id, new String[] { "empty", "miniheart.red", "heart" }, new String[] { "canister_empty", "miniheart_red", "canister_heart" }, "");
		this.setMaxStackSize(10);
	}

	@Override
	public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote && stack.getItemDamage() == 2)
		{
			TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
			if (stats != null)
			{
				ArmorExtended armor = stats.armor;
				ItemStack slotStack = armor.getStackInSlot(6);
				if (slotStack == null)// || slotStack.getItem() == this)
				{
					armor.setInventorySlotContents(6, new ItemStack(this, 1, 2));
					stack.stackSize--;
				}
				else if (slotStack.getItem() == this && slotStack.stackSize < this.maxStackSize)
				{
					slotStack.stackSize++;
					stack.stackSize--;
				}
				armor.recalculateHealth(player, stats);
			}
		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		switch (stack.getItemDamage())
		{
		case 0:
			list.add("Crafting Item");
			break;
		case 1:
			list.add("Crafting Item");
			list.add("Part of low-level Heart Canisters");
			break;
		case 2:
			list.add("Accessory");
			list.add("Permanent health increase");
			break;
		}
	}

}
