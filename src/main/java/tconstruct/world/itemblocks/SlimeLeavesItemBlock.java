package tconstruct.world.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class SlimeLeavesItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue" };

    public SlimeLeavesItemBlock(Block b)
    {
        super(b, "block.slime.leaves", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
