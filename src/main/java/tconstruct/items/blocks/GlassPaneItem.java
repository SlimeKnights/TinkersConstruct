package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class GlassPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "pure.pane", "soul.pane", "soul.pure.pane" };

    public GlassPaneItem(int id)
    {
        super(id, "block.glass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
