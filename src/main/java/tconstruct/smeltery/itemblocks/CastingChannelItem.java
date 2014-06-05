package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class CastingChannelItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "Channel" };

    public CastingChannelItem(Block b)
    {
        super(b, "Smeltery", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
