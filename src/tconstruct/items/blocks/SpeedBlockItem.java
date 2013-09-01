package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SpeedBlockItem extends ItemBlock
{
    public static final String blockType[] = { "brownstone.rough", "brownstone.rough.road", "brownstone.smooth", "brownstone.smooth.brick", "brownstone.smooth.road", "brownstone.smooth.fancy",
            "brownstone.smooth.chiseled" };

    public SpeedBlockItem(int id)
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
        return (new StringBuilder()).append("block.").append(blockType[pos]).toString();
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int metadata = stack.getItemDamage();
        if (metadata == 1 || metadata == 4)
            list.add("You run pretty fast on it");
        else
            list.add("You run a bit faster on it");
    }
}
