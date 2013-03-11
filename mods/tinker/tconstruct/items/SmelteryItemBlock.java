package mods.tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SmelteryItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Controller", "Drain", "Brick", "Gague", "Window"
    };

    public SmelteryItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata(int meta)
    {
        return meta;
    }

    public String getUnlocalizedName(ItemStack itemstack)
    {
        return (new StringBuilder()).append("Smeltery.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
