package tconstruct.smeltery.itemblocks;

import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GlassBlockItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "pure" };// , "soul",
                                                         // "soul.pure" };

    public GlassBlockItem(Block b)
    {
        super(b, "block.glass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
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
