package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class StainedGlassClearPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan",
            "purple", "blue", "brown", "green", "red", "black" };

    public StainedGlassClearPaneItem(int id)
    {
        super(id, "block.stainedglass", "pane", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
