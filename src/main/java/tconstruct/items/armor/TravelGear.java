package tconstruct.items.armor;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import tconstruct.TConstruct;
import tconstruct.client.TControls;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelGear extends ArmorCore
{
    String textureName;

    public TravelGear(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
        this.setMaxDamage(1035);
    }

    @Override
    public String getModifyType ()
    {
        return "Clothing";
    }

    @Override
    public ArmorProperties getProperties (EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        //Priority, absorbRatio, max
        if (!armor.hasTagCompound() || source.isUnblockable())
            return new ArmorProperties(0, 0, 0);

        NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getBaseTagName());
        if (tags.getBoolean("Broken"))
            return new ArmorProperties(0, 0, 0);

        float maxDurability = tags.getInteger("TotalDurability");
        float currentDurability = maxDurability - tags.getInteger("Damage");
        float ratio = currentDurability / maxDurability;
        double base = tags.getDouble("BaseDefense");
        double max = tags.getDouble("MaxDefense");
        double current = (max - base) * ratio + base;

        return new ArmorProperties(0, current / 100, 100);
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack armor, DamageSource source, int damage, int slot)
    {
        if (armor.hasTagCompound())
        {
            NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getBaseTagName());
            if (!tags.getBoolean("Broken"))
            {
                int maxDurability = tags.getInteger("TotalDurability");
                int currentDurability = tags.getInteger("Damage");
                if (currentDurability + damage > maxDurability)
                {
                    tags.setInteger("Damage", 0);
                    tags.setBoolean("Broken", true);
                    armor.setItemDamage(0);
                }
                else
                {
                    tags.setInteger("Damage", currentDurability + damage);
                    armor.setItemDamage(currentDurability + damage);
                }
            }
        }
    }

    @Override
    public int getArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        /*if (slot == 2)
            return 20;
        return 0;*/
        if (!armor.hasTagCompound())
            return 0;

        NBTTagCompound armorTag = armor.getTagCompound().getCompoundTag(getBaseTagName());
        if (armorTag.getBoolean("Broken"))
            return 0;

        float max = armorTag.getInteger("TotalDurability");
        float current = max - armorTag.getInteger("Damage");
        float amount = current / max * 5 + 0.09F;
        if (slot == 2 && amount < 1)
            amount = 1;
        return (int) amount;
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        if (armorPart == EnumArmorPart.Chest)
        {
            if (player.isInWater())
            {
                player.motionX *= 1.2D;
                if (player.motionY > 0.0D)
                {
                    player.motionY *= 1.2D;
                }
                player.motionZ *= 1.2D;
                double maxSpeed = 0.2D;
                if (player.motionX > maxSpeed)
                {
                    player.motionX = maxSpeed;
                }
                else if (player.motionX < -maxSpeed)
                {
                    player.motionX = -maxSpeed;
                }
                if (player.motionY > maxSpeed)
                {
                    player.motionY = maxSpeed;
                }
                if (player.motionZ > maxSpeed)
                {
                    player.motionZ = maxSpeed;
                }
                else if (player.motionZ < -maxSpeed)
                {
                    player.motionZ = -maxSpeed;
                }
            }
        }

        /*else if (armorPart == EnumArmorPart.Feet)
        {
            if (player.stepHeight < 1.0f)
                player.stepHeight = 1.0f;
        }*/

        else if (armorPart == EnumArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats.activeGoggles)
            {
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
                //player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1, 0, true));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected Icon[] modifiers;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "goggles" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "wings" : this.armorType == 3 ? "boots" : "helmet"));
        registerModifiers(iconRegister);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        if (slot == 2)
            return "tinker:textures/armor/" + textureName + "_" + 2 + ".png";
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
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

        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return TProxyClient.vest;
        if (armorSlot == 2)
            return TProxyClient.wings;
        if (armorSlot == 3)
            return TProxyClient.bootbump;
        return null;
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

        switch (armorPart)
        {
        case Head:
            list.add("\u00a76Ability: Clear Vision");
            list.add("\u00a76Toggle with: " + GameSettings.getKeyDisplayString(TControls.toggleGoggles.keyCode));
            break;
        case Chest:
            list.add("\u00a76Ability: Swift Swim");
            break;
        case Legs:
            list.add("\u00a76Ability: Featherfall");
            break;
        case Feet:
            list.add("\u00a76Ability: High Step");
            break;
        default:
        }

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
