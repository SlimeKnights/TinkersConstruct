package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class SlimeGelItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue", "green", "purple", "magma", "yellow", "red", "metal" };

    public SlimeGelItemBlock(int id)
    {
        super(id, "block.slime.congealed", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
