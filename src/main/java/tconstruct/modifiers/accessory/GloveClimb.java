package tconstruct.modifiers.accessory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;

/* Adds a boolean NBTTag */

public class GloveClimb extends ItemModifier
{

    public GloveClimb(ItemStack[] items)
    {
        super(items, 3, "WallClimb");
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("TinkerAccessory");
        return tags.getInteger("Modifiers") >= 3 && !tags.getBoolean(key);
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("TinkerAccessory");

        tags.setBoolean(key, true);

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 3;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(tool, "\u00a7aWall Climb", "\u00a7aWall Climb");
    }
}
