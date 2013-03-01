package tinker.tconstruct.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.w3c.dom.Document;

import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;
import tinker.tconstruct.TGuiHandler;
import tinker.tconstruct.client.TProxyClient;
import tinker.tconstruct.client.gui.GuiManual;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class PatternManual extends CraftingItem
{

	public PatternManual(int id)
	{
		super(id, 176, TContent.craftingTexture);
		setItemName("tconstruct.PatternManual");
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
