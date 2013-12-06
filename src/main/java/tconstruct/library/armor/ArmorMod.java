package tconstruct.library.armor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMod;

public abstract class ArmorMod extends ToolMod
{
    public ArmorMod(ItemStack[] items, int effect, String dataKey)
    {
        super(items, effect, dataKey);
    }
    
    @Override
    protected String getTagName()
    {
        return "TinkerArmor";
    }
    
    public boolean validArmorType(ArmorCore armor)
    {
        return true;
    }
}
