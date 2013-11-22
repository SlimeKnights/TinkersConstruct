package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SpeedSlabItem extends ItemBlock
{
    public static final String blockType[] = { "brownstone.rough", "brownstone.rough.road", "brownstone.smooth", "brownstone.smooth.brick", "brownstone.smooth.road", "brownstone.smooth.fancy",
            "brownstone.smooth.chiseled" };

    public SpeedSlabItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.").append(blockType[pos]).append(".slab").toString();
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int metadata = stack.getItemDamage() % 8;
        if (metadata == 1 || metadata == 4)
            list.add(StatCollector.translateToLocal("speedblock.slab1.tooltip"));
        else
            list.add(StatCollector.translateToLocal("speedblock.slab2.tooltip"));
    }
}
