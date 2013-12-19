package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class CastingChannelItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "Channel" };

    public CastingChannelItem(int id)
    {
        super(id, "Smeltery", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
