package tconstruct.world.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class SlimeSaplingItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "bluegreen" };

    public SlimeSaplingItemBlock(Block b)
    {
        super(b, "block.slime.sapling", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
