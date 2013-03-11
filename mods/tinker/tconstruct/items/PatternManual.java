package mods.tinker.tconstruct.items;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.TGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PatternManual extends CraftingItem
{
	static String[] name = new String[] {"manualDiary"};
	public PatternManual(int id)
	{
		super(id, name, name);
		setUnlocalizedName("tconstruct.manualDiary");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		player.openGui(TConstruct.instance, TGuiHandler.manualGui, world, 0, 0, 0);
		/*Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.isClient())
			FMLClientHandler.instance().displayGuiScreen(player, new GuiManual(player.getCurrentEquippedItem(), getManualFromStack(stack)));*/
        return stack;
    }
	
	
}
