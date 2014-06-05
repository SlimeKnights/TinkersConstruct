package tconstruct.world.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class SlimeGelItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue", "green", "purple", "magma", "yellow", "red", "metal" };

    public SlimeGelItemBlock(Block b)
    {
        super(b, "block.slime.congealed", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
