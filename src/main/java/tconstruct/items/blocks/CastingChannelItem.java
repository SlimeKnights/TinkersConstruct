package tconstruct.items.blocks;

import net.minecraft.block.Block;
import mantle.blocks.abstracts.MultiItemBlock;

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
