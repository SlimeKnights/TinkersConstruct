package tconstruct.items.blocks;

import java.util.List;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class SpeedBlockItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "brownstone.rough", "brownstone.rough.road", "brownstone.smooth", "brownstone.smooth.brick", "brownstone.smooth.road", "brownstone.smooth.fancy",
            "brownstone.smooth.chiseled" };

    public SpeedBlockItem(int id)
    {
        super(id, "block", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int metadata = stack.getItemDamage();
        if (metadata == 1 || metadata == 4)
            list.add(StatCollector.translateToLocal("speedblock1.tooltip"));
        else
            list.add(StatCollector.translateToLocal("speedblock2.tooltip"));
    }
}
