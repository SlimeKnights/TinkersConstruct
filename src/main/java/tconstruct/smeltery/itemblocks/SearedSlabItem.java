package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class SearedSlabItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "brick", "stone", "cobble", "paver", "road", "fancy", "square", "creeper" };

    public SearedSlabItem(Block b)
    {
        super(b, "block.searedstone.slab", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
