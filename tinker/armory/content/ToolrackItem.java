package tinker.armory.content;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ToolrackItem extends ItemBlock
{
	public ToolrackItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
    
    @Override
    public int getMetadata(int md)
    {
        return md;
    }
    
    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append("block.").append(blockType[itemstack.getItemDamage()]).append("Toolrack").toString();
    }
    public static final String blockType[] =
    {
        "stone", "stonebrick", "brick", "obsidian"
    };
}
