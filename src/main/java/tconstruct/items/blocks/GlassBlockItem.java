package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class GlassBlockItem extends ItemBlock
{
    public static final String blockType[] = { "glass.pure" };//, "glass.soul", "glass.soul.pure" };

    public GlassBlockItem(int id)
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
        return (new StringBuilder()).append("block.").append(blockType[pos]).toString();
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.hasTagCompound() && stack.getItemDamage() == 0)
        {
            NBTTagCompound contentTags = stack.getTagCompound().getCompoundTag("Contents");
            if (contentTags != null)
            {
                ItemStack contents = ItemStack.loadItemStackFromNBT(contentTags);
                if (contents != null)
                {
                    list.add("Inventory: " + contents.getDisplayName());
                    list.add("Amount: " + contents.stackSize);
                }
            }
            NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
            if (camoTag != null)
            {
                ItemStack camo = ItemStack.loadItemStackFromNBT(camoTag);
                if (camo != null)
                    list.add("Camoflauge: " + camo.getDisplayName());
            }

            if (stack.getTagCompound().hasKey("Placement"))
            {
                String string = getDirectionString(stack.getTagCompound().getByte("Placement"));
                list.add("Placement Direction: " + string);
            }
        }
    }

    String getDirectionString (byte key)
    {
        if (key == 0)
            return ("Up");
        if (key == 1)
            return ("Right");
        if (key == 2)
            return ("Down");

        return "Left";
    }
}
