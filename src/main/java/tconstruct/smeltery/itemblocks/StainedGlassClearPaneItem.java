package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class StainedGlassClearPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public StainedGlassClearPaneItem(Block b)
    {
        super(b, "block.stainedglass", "pane", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
