package tconstruct.items.accessory;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import tconstruct.library.IAccessory;
import tconstruct.library.IModifyable;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Glove extends Item implements IAccessory, IModifyable
{
    public Glove(int par1)
    {
        super(par1);
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @Override
    public String getBaseTag ()
    {
        return "TinkerAccessory";
    }
    
    @Override
    public String getModifyType()
    {
        return "Accessory";
    }

    Icon[] modifiers;

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon("tinker:armor/glovebase_leather");
        modifiers = new Icon[4];
        modifiers[0] = iconRegister.registerIcon("tinker:armor/glove_guard");
        modifiers[1] = iconRegister.registerIcon("tinker:armor/glove_speedaura");
        modifiers[2] = iconRegister.registerIcon("tinker:armor/glove_spines");
        modifiers[3] = iconRegister.registerIcon("tinker:armor/glove_sticky");
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int renderpass)
    {
        if (renderpass > 0)
        {
            return 0xFFFFFF;
        }
        else
        {
            int color = this.getColor(stack);

            if (color < 0)
            {
                color = 0xFFFFFF;
            }

            return color;
        }
    }

    public int getColor (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags == null)
        {
            return 0xA0F540;
        }
        else
        {
            NBTTagCompound display = tags.getCompoundTag("display");
            return display == null ? 0xA06540 : (display.hasKey("color") ? display.getInteger("color") : 0xA06540);
        }
    }

    @Override
    public boolean canEquipItem (ItemStack item, int slot)
    {
        return slot == 1;
    }
}
