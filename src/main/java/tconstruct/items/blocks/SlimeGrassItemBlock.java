package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class SlimeGrassItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue", "dirt" };

    public SlimeGrassItemBlock(int id)
    {
        super(id, "block.slime.grass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
