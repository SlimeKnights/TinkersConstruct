package mods.tinker.tconstruct.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.util.network.TGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Manual extends CraftingItem
{
	static String[] name = new String[] { "beginner", "toolstation", "smeltery" };
	static String[] textureName = new String[] { "tinkerbook_diary", "tinkerbook_toolstation", "tinkerbook_smeltery" };
	public Manual(int id)
	{
		super(id, name, textureName, "");
		setUnlocalizedName("tconstruct.manual");
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
	
	@SideOnly(Side.CLIENT)
	public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		switch (stack.getItemDamage())
		{
		case 0:
			list.add("\u00a7oBy: Skyla");
			break;
		case 1:
			list.add("\u00a7oBy: Skyla");
			break;
		case 2:
			list.add("\u00a7oBy: Thruul M'gon");
			break;
		}
	}
}
