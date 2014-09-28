package tconstruct.tools.items;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.books.BookData;
import mantle.client.gui.GuiManual;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.achievements.TAchievements;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;

public class Manual extends CraftingItem
{
    static String[] name = new String[] { "beginner", "toolstation", "smeltery", "diary" };
    static String[] textureName = new String[] { "tinkerbook_diary", "tinkerbook_toolstation", "tinkerbook_smeltery", "tinkerbook_blue" };

    public Manual()
    {
        super(name, textureName, "", "tinker", TConstructRegistry.materialTab);
        setUnlocalizedName("tconstruct.manual");

    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        TAchievements.triggerAchievement(player, "tconstruct.beginner");

        Side side = FMLCommonHandler.instance().getEffectiveSide();
        player.openGui(TConstruct.instance, mantle.client.MProxyClient.manualGuiID, world, 0, 0, 0);
        FMLClientHandler.instance().displayGuiScreen(player, new GuiManual(stack, getData(stack)));
        return stack;
    }

    private BookData getData (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return TProxyClient.manualData.beginner;
        case 1:
            return TProxyClient.manualData.toolStation;
        case 2:
            return TProxyClient.manualData.smeltery;
        default:
            return TProxyClient.manualData.diary;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add("\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"));
            break;
        case 1:
            list.add("\u00a7o" + StatCollector.translateToLocal("manual2.tooltip"));
            break;
        case 2:
            list.add("\u00a7o" + StatCollector.translateToLocal("manual3.tooltip"));
            break;
        }
    }
}
