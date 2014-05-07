package tconstruct.items.armor;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ToolCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LeatherSuit extends ArmorCore
{
    String textureName;

    public LeatherSuit(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part, "Clothing", texture);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
        this.setMaxDamage(1035);
    }

    //Temporary?
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return new ItemStack(Item.leather);
    }

    @Override
    protected double getBaseDefense ()
    {
        switch (armorPart)
        {
        case Head:
            return 2;
        case Chest:
            return 4;
        case Legs:
            return 4;
        case Feet:
            return 2;
        }
        return 0;
    }

    @Override
    protected double getMaxDefense ()
    {
        switch (armorPart)
        {
        case Head:
            return 6;
        case Chest:
            return 10;
        case Legs:
            return 8;
        case Feet:
            return 6;
        }
        return 0;
    }
    
    @Override
    protected int getDurability()
    {
        return 1035;
    }

    @SideOnly(Side.CLIENT)
    protected Icon[] modifiers;
    protected Icon overlayIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "hat" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "pants" : this.armorType == 3 ? "boots" : "hat"));
        this.overlayIcon = iconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "hat" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "pants" : this.armorType == 3 ? "boots" : "hat") + "_overlay");
        registerModifiers(iconRegister);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
    {
        if (type == "overlay")
            return "tinker:textures/armor/" + textureName + "_" + (slot == 2 ? 2 : 1) + "_b.png";
        return "tinker:textures/armor/" + textureName + "_" + (slot == 2 ? 2 : 1) + ".png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        if (renderPass > 1)
        {
            if (stack.hasTagCompound())
            {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag(getBaseTagName());
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

        if (renderPass == 0)
            return itemIcon;
        else
            return overlayIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        /*if (armorSlot == 1)
            return TProxyClient.vest;*/
        if (armorSlot == 3)
            return TProxyClient.bootbump;
        return null;
    }

    @Override
    public int getColorFromItemStack (ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
            return 0xFFFFFF;

        int color = this.getColor(stack);
        if (color < 0)
            color = 0xFFFFFF;

        return color;
    }

    @Override
    public int getColor (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags == null)
            return 0xA06540;

        else
        {
            NBTTagCompound display = tags.getCompoundTag("display");
            return display == null ? 0xA06540 : (display.hasKey("color") ? display.getInteger("color") : 0xA05530);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getDefaultItem());
    }

    public ItemStack getDefaultItem ()
    {
        ItemStack gear = new ItemStack(this.itemID, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Modifiers", 3);
        double flat = 0; //Light armor, always 0
        double base = 0;
        double max = 0;
        switch (armorPart)
        {
        case Head:
            base = 0;
            max = 4;
            break;
        case Chest:
            base = 4;
            max = 10;
            break;
        case Legs:
            base = 2;
            max = 8;
            break;
        case Feet:
            base = 2;
            max = 6;
            break;
        }
        tag.setDouble("DamageReduction", flat);
        tag.setDouble("BaseDefense", base);
        tag.setDouble("MaxDefense", max);

        int baseDurability = 1035;

        tag.setInteger("Damage", 0); //Damage is damage to the armor
        tag.setInteger("TotalDurability", baseDurability);
        tag.setInteger("BaseDurability", baseDurability);
        tag.setInteger("BonusDurability", 0); //Modifier
        tag.setFloat("ModDurability", 0f); //Modifier
        tag.setBoolean("Broken", false);
        tag.setBoolean("Built", true);

        baseTag.setTag(getBaseTagName(), tag);
        gear.setTagCompound(baseTag);
        return gear;
    }

    DecimalFormat df = new DecimalFormat("##.#");

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag(getBaseTagName());
        double protection = 0;
        if (!tags.getBoolean("Broken"))
        {
            float maxDurability = tags.getInteger("TotalDurability");
            float currentDurability = maxDurability - tags.getInteger("Damage");
            float ratio = currentDurability / maxDurability;
            double base = tags.getDouble("BaseDefense");
            double max = tags.getDouble("MaxDefense");
            protection = (max - base) * ratio + base;
        }
        if (protection > 0)
            list.add("\u00a77Protection: " + df.format(protection) + "%");
        else
            list.add("\u00A7oBroken");

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
