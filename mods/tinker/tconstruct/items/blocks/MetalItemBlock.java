package mods.tinker.tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class MetalItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Tin", "Aluminum", "AlBrass", "Alumite", "Steel", "Ender" };

    public MetalItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("StorageMetals.").append(blockType[pos]).toString();
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("Usable for Beacon bases");
    }
}
