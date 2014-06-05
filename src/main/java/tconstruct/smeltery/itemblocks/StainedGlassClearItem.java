package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class StainedGlassClearItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public StainedGlassClearItem(Block b)
    {
        super(b, "block.stainedglass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
