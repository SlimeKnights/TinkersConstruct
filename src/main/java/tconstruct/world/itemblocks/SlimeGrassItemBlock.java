package tconstruct.world.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class SlimeGrassItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue", "dirt" };

    public SlimeGrassItemBlock(Block b)
    {
        super(b, "block.slime.grass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
