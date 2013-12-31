package tconstruct.items.blocks;

import net.minecraft.block.Block;
import mantle.blocks.abstracts.MultiItemBlock;

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
