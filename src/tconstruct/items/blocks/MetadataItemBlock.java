package tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;

public class MetadataItemBlock extends ItemBlock
{
    public MetadataItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }
}
