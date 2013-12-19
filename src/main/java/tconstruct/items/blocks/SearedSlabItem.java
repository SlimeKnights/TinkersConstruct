package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SearedSlabItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "brick", "stone", "cobble", "paver", "road", "fancy", "square", "creeper" };

    public SearedSlabItem(int id)
    {
        super(id, "block.searedstone.slab", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
