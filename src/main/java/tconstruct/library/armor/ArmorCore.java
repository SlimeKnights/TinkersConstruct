package tconstruct.library.armor;

import java.text.DecimalFormat;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import tconstruct.client.TControls;
import tconstruct.library.modifier.IModifyable;

public abstract class ArmorCore extends ItemArmor implements ISpecialArmor, IModifyable
{
    public final EnumArmorPart armorPart;
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDispenseArmorCopy();
    public final int baseProtection;
    protected final String modifyType;
    protected final String textureName;

    public ArmorCore(int id, int baseProtection, EnumArmorPart part, String type, String textureName)
    {
        super(id, EnumArmorMaterial.CHAIN, 0, part.getPartId());
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.armorPart = part;
        this.baseProtection = baseProtection;
        this.modifyType = type;
        this.textureName = textureName;
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }
    
    //Temporary?
    public abstract ItemStack getRepairMaterial (ItemStack input);

    public String getArmorName ()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getBaseTagName ()
    {
        return "TinkerArmor";
    }

    @Override
    public String getModifyType ()
    {
        return modifyType;
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "armor" };
    }

    public ItemStack onItemRightClick (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        int i = EntityLiving.getArmorPosition(par1ItemStack) - 1;
        ItemStack itemstack1 = par3EntityPlayer.getCurrentArmor(i);

        if (itemstack1 == null)
        {
            par3EntityPlayer.setCurrentItemOrArmor(i + 1, par1ItemStack.copy()); //Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.
            par1ItemStack.stackSize = 0;
        }

        return par1ItemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        if (slot == 2)
            return "tinker:textures/armor/" + textureName + "_" + 2 + ".png";
        return "tinker:textures/armor/" + textureName + "_" + 1 + ".png";
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
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses (int metadata)
    {
        return 4;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return false;
    }

    //ISpecialArmor overrides
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
    public int getArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        if (slot != 1)
        {
            ItemStack stack = player.getCurrentArmor(1);
            if (stack != null && stack.getItem() instanceof ArmorCore)
                return 0;
            return disconnectedArmorDisplay(player, armor, slot);
        }

        return combinedArmorDisplay(player, armor);
    }

    protected int disconnectedArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
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

    protected int combinedArmorDisplay (EntityPlayer player, ItemStack legs)
    {
        ItemStack[] armors = new ItemStack[] { player.getCurrentArmor(3), player.getCurrentArmor(2), legs, player.getCurrentArmor(0) };
        int types = 0;
        int max = 0;
        int damage = 0;
        boolean anyAlive = false;
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = armors[i];
            if (stack != null && stack.hasTagCompound())
            {
                NBTTagCompound armorTag = stack.getTagCompound().getCompoundTag(getBaseTagName());
                if (stack.getItem() instanceof ArmorCore)
                {
                    types++;
                    max += armorTag.getInteger("TotalDurability");
                    if (armorTag.getBoolean("Broken"))
                    {
                        damage += armorTag.getInteger("TotalDurability");
                    }
                    else
                    {
                        damage += armorTag.getInteger("Damage");
                        anyAlive = true;
                    }
                }
            }
        }
        float ratio = ((float) max - (float) damage) / (float) max * (types * 5) + 0.1f;
        int minimum = anyAlive ? 1 : 0;
        if (ratio < minimum)
            ratio = minimum;
        return (int) ratio;
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
                    entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, "random.break", 1f, 1f, true);
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
        double flat = getFlatDefense(); 
        double base = getBaseDefense();
        double max = getMaxDefense();
        tag.setDouble("DamageReduction", flat);
        tag.setDouble("BaseDefense", base);
        tag.setDouble("MaxDefense", max);

        int baseDurability = getDurability();

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
    
    protected double getFlatDefense()
    {
        return 0;
    }
    protected abstract double getBaseDefense();
    protected abstract double getMaxDefense();
    protected abstract int getDurability();

    // Vanilla overrides
    public boolean isItemTool (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean getIsRepairable (ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return false;
    }

    public boolean isRepairable ()
    {
        return false;
    }

    public int getItemEnchantability ()
    {
        return 0;
    }

    public boolean isFull3D ()
    {
        return true;
    }

    public boolean isValidArmor (ItemStack stack, int armorType, Entity entity)
    {
        return this.armorPart.getPartId() == armorType;
    }

    /* Proper stack damage */
    public int getItemMaxDamageFromStack (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }

        return tags.getCompoundTag(getBaseTagName()).getInteger("TotalDurability");
    }

    public int getItemMaxDamageFromStackForDisplay (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }

        return tags.getCompoundTag(getBaseTagName()).getInteger("Damage");
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