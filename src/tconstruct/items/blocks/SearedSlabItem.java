package tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SearedSlabItem extends ItemBlock
{
    public static final String blockType[] = { "brick", "stone", "cobble", "paver", "road", "fancy", "square", "creeper" };

    public SearedSlabItem(int id)
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
        return (new StringBuilder()).append("block.searedstone.slab.").append(blockType[pos]).toString();
    }
}
