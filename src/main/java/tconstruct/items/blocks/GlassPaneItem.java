package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class GlassPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "pure", "soul", "soul.pure" };

    public GlassPaneItem(int id)
    {
        super(id, "block.glass", "pane", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
