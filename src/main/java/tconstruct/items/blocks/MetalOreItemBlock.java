package tconstruct.items.blocks;

import java.util.List;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class MetalOreItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "NetherSlag", "Cobalt", "Ardite", "Copper", "Tin", "Aluminum", "Slag" };

    public MetalOreItemBlock(int id)
    {
        super(id, "MetalOre", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Liquid");
            if (liquidTag != null)
            {
                list.add("Contains " + liquidTag.getString("LiquidName"));
                list.add(liquidTag.getInteger("Amount") + " mB");
            }
        }
    }
}
