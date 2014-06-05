package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class GlassPaneItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "pure", "soul", "soul.pure" };

    public GlassPaneItem(Block b)
    {
        super(b, "block.glass", "pane", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
