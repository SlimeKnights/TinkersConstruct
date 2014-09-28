package tconstruct.world.itemblocks;

import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MetalOreItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "NetherSlag", "Cobalt", "Ardite", "Copper", "Tin", "Aluminum", "Slag" };

    public MetalOreItemBlock(Block b)
    {
        super(b, "MetalOre", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
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
