package tconstruct.items;

import tconstruct.achievements.TAchievements;

import java.util.List;

import tconstruct.TConstruct;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Manual extends CraftingItem
{
    static String[] name = new String[] { "beginner", "toolstation", "smeltery", "diary" };
    static String[] textureName = new String[] { "tinkerbook_diary", "tinkerbook_toolstation", "tinkerbook_smeltery", "tinkerbook_blue" };

    public Manual(int id)
    {
        super(id, name, textureName, "");
        setUnlocalizedName("tconstruct.manual");
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
    	player.addStat(TAchievements.achievements.get("tconstruct.beginner"), 1);
        player.openGui(TConstruct.instance, TConstruct.proxy.manualGuiID, world, 0, 0, 0);
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
            list.add("\u00a7o"+StatCollector.translateToLocal("manual1.tooltip"));
            break;
        case 1:
            list.add("\u00a7o"+StatCollector.translateToLocal("manual2.tooltip"));
            break;
        case 2:
            list.add("\u00a7o"+StatCollector.translateToLocal("manual3.tooltip"));
            break;
        }
    }
}
