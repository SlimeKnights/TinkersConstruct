package tconstruct.items.accessory;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import tconstruct.library.IAccessory;
import tconstruct.library.IModifyable;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Glove extends Item implements IAccessory, IModifyable
{
    public Glove(int par1)
    {
        super(par1);
        this.setCreativeTab(TConstructRegistry.materialTab);
        this.setMaxStackSize(1);
    }

    @Override
    public String getBaseTag ()
    {
        return "TinkerAccessory";
    }

    @Override
    public String getModifyType ()
    {
        return "Accessory";
    }

    final static String[] traits = new String[] {"accessory"};
    @Override
    public String[] getTraits ()
    {
        return traits;
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 1;
    }
    
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        ItemStack glove = new ItemStack(this);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Built", true);
        tag.setInteger("Modifiers", 5);
        baseTag.setTag(getBaseTag(), tag);
        glove.setTagCompound(baseTag);
        list.add(glove);
    }

    /* Color */

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

    /* Icons */

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
    @Override
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
        {
            if (stack.hasTagCompound())
            {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag("TinkerAccessory");
                if (renderPass == 1 && tags.hasKey("Effect1"))
                {
                    return modifiers[tags.getInteger("Effect1")];
                }
                if (renderPass == 2 && tags.hasKey("Effect2"))
                {
                    return modifiers[tags.getInteger("Effect2")];
                }
                if (renderPass == 3 && tags.hasKey("Effect3"))
                {
                    return modifiers[tags.getInteger("Effect3")];
                }
            }
            return ToolCore.blankSprite;
        }

        return itemIcon;
    }
    
    /* Tooltips */
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.hasKey("TinkerAccessory"))
        {
            boolean broken = tags.getCompoundTag("TinkerAccessory").getBoolean("Broken");
            if (broken)
                list.add("\u00A7oBroken");
            else
            {
                boolean displayToolTips = true;
                int tipNum = 0;
                while (displayToolTips)
                {
                    tipNum++;
                    String tooltip = "Tooltip" + tipNum;
                    if (tags.getCompoundTag("TinkerAccessory").hasKey(tooltip))
                    {
                        String tipName = tags.getCompoundTag("TinkerAccessory").getString(tooltip);
                        if (!tipName.equals(""))
                            list.add(tipName);
                    }
                    else
                        displayToolTips = false;
                }
            }
        }
    }
}
