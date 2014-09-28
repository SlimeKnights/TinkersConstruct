package tconstruct.world.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class StrangeFood extends SpecialFood
{
    public StrangeFood()
    {
        super(new int[] { 2, 2, 4 }, new float[] { 1f, 1f, 0.6f }, new String[] { "edibleslime", "edibleblood", "bacon" }, new String[] { "food/edibleslime", "food/edibleblood", "food/bacon" });
    }

    @Override
    protected void onFoodEaten (ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.getItemDamage() == 1)
            player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 20 * 15, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int type = stack.getItemDamage();
        switch (type)
        {
        case 0:
            list.add("\u00a7b\u00a7o" + StatCollector.translateToLocal("strangefood1.tooltip"));
            list.add("\u00a7b\u00a7o" + StatCollector.translateToLocal("strangefood2.tooltip"));
            break;
        case 1:
            list.add("\u00a74\u00a7o" + StatCollector.translateToLocal("strangefood3.tooltip"));
            list.add("\u00a74\u00a7o" + StatCollector.translateToLocal("strangefood4.tooltip"));
            break;
        case 2:
            list.add("\u00a7c" + StatCollector.translateToLocal("strangefood7.tooltip"));
            break;
        }
    }
}
