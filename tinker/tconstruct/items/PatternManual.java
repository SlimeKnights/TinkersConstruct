package tinker.tconstruct.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.TConstructGuiHandler;

public class PatternManual extends CraftingItem
{

	public PatternManual(int id)
	{
		super(id, 144, TConstructContent.craftingTexture);
		setItemName("tconstruct.PatternManual");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		player.openGui(TConstruct.instance, TConstructGuiHandler.manualGui, world, 0, 0, 0);
        return stack;
    }
}
