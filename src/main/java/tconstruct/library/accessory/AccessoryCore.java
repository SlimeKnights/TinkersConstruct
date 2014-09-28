package tconstruct.library.accessory;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.ToolCore;

public abstract class AccessoryCore extends Item implements IAccessory, IModifyable
{
    /**
     * Override getArmorModel() to have render on the player.
     */
    protected String texture;

    public AccessoryCore(String texture)
    {
        super();
        this.setCreativeTab(TConstructRegistry.equipableTab);
        this.setMaxStackSize(1);
        this.texture = texture;
    }

    @Override
    public String getBaseTagName ()
    {
        return "TinkerAccessory";
    }

    @Override
    public String getModifyType ()
    {
        return "Accessory";
    }

    final static String[] traits = new String[] { "accessory" };

    @Override
    public String[] getTraits ()
    {
        return traits;
    }

    /*public void getSubItems (int id, CreativeTabs tab, List list)
    {
        ItemStack glove = new ItemStack(this);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Built", true);
        tag.setInteger("Modifiers", 5);
        baseTag.setTag(getBaseTag(), tag);
        glove.setTagCompound(baseTag);
        list.add(glove);
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getDefaultItem());
    }

    public ItemStack getDefaultItem ()
    {
        ItemStack gear = new ItemStack(this, 1, 0);
        NBTTagCompound itemTag = new NBTTagCompound();

        int baseDurability = 500;

        itemTag.setInteger("Damage", 0); //Damage is damage to the tool
        itemTag.setInteger("TotalDurability", baseDurability);
        itemTag.setInteger("BaseDurability", baseDurability);
        itemTag.setInteger("BonusDurability", 0); //Modifier
        itemTag.setFloat("ModDurability", 0f); //Modifier
        itemTag.setInteger("Modifiers", 5);
        itemTag.setBoolean("Broken", false);
        itemTag.setBoolean("Built", true);

        NBTTagCompound baseTag = new NBTTagCompound();
        baseTag.setTag(getBaseTagName(), itemTag);
        gear.setTagCompound(baseTag);
        return gear;
    }

    /* Icons */

    @SideOnly(Side.CLIENT)
    protected IIcon[] modifiers;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon("tinker:" + texture);
        registerModifiers(iconRegister);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IIconRegister iconRegister)
    {
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
    public IIcon getIcon (ItemStack stack, int renderPass)
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
        if (tags.hasKey(getBaseTagName()))
        {
            tags = stack.getTagCompound().getCompoundTag(getBaseTagName());
            boolean broken = tags.getBoolean("Broken");
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
                    if (tags.hasKey(tooltip))
                    {
                        String tipName = tags.getString(tooltip);
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
