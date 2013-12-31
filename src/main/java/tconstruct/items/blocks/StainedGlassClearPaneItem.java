package tconstruct.items.blocks;

import net.minecraft.block.Block;
import mantle.blocks.abstracts.MultiItemBlock;

public class StainedGlassClearPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan",
            "purple", "blue", "brown", "green", "red", "black" };

    public StainedGlassClearPaneItem(Block b)
    {
        super(b, "block.stainedglass", "pane", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
