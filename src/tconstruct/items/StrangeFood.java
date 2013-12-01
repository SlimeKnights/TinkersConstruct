package tconstruct.items;

import java.util.List;

import net.minecraft.util.StatCollector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class StrangeFood extends SpecialFood
{
    public StrangeFood(int id)
    {
        super(id, new int[] { 2 }, new float[] { 1f }, new String[] { "edibleslime" }, new String[] { "food/edibleslime" });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("\u00a7b\u00a7o"+StatCollector.translateToLocal("strangefood1.tooltip"));
        list.add("\u00a7b\u00a7o"+StatCollector.translateToLocal("strangefood2.tooltip"));
    }
}
