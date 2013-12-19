package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;


public class SlimeSaplingItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "bluegreen" };

    public SlimeSaplingItemBlock(int id)
    {
        super(id, "block.slime.sapling", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
