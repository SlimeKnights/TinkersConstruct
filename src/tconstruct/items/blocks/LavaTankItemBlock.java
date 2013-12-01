package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class LavaTankItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Tank", "Gague", "Window" };

    public LavaTankItemBlock(int id)
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
        return (new StringBuilder()).append("LavaTank.").append(blockType[pos]).toString();
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null)
            {
                list.add(StatCollector.translateToLocal("searedtank1.tooltip") + StatCollector.translateToLocal(liquidTag.getString("FluidName")));
                list.add(liquidTag.getInteger("Amount") + " mB");
            }
        }
        else
        {
            list.add(StatCollector.translateToLocal("searedtank2.tooltip"));
        }
    }
}
