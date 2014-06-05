package tconstruct.common.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class MetadataItemBlock extends ItemBlock
{
    public MetadataItemBlock(Block b)
    {
        super(b);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata (int meta)
    {
        return meta;
    }
}
