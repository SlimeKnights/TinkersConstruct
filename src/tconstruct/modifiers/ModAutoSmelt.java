package tconstruct.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModAutoSmelt extends ModBoolean
{

    public ModAutoSmelt(ItemStack[] items, int effect, String tag, String c, String tip)
    {
        super(items, effect, tag, c, tip);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Silk Touch"))
            return false;
        return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
    }

}
