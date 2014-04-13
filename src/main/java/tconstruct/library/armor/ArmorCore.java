package tconstruct.library.armor;

import net.minecraft.block.BlockDispenser;
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

public abstract class ArmorCore extends ItemArmor implements ISpecialArmor
{
    public static final String SET_NAME = "TinkerArmor";
    public final EnumArmorPart armorPart;
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDispenseArmorCopy();
    public final int baseProtection;

    public ArmorCore(int id, int baseProtection, EnumArmorPart part)
    {
        super(id, EnumArmorMaterial.CHAIN, 0, part.getPartId());
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.armorPart = part;
        this.baseProtection = baseProtection;
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }

    public String getArmorName ()
    {
        return this.getClass().getSimpleName();
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

    //ISpecialArmor overrides
    @Override
    public ArmorProperties getProperties (EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        if (player.worldObj.isRemote)
        {
            return new ArmorProperties(0, 0, baseProtection);
        }

        NBTTagCompound tags = armor.getTagCompound();

        if (tags == null)
        {
            return new ArmorProperties(0, damage / baseProtection, baseProtection);
        }

        NBTTagCompound data = tags.getCompoundTag(SET_NAME);
        double amount = (data.getInteger("defense") / damage) + (data.getDouble("protection") / 100);
        if (source.isUnblockable())
            amount = 0;
        clamp_double(amount, 0, 1);
        return new ArmorProperties(0, amount, 100);
    }

    public static double clamp_double (double par0, double par1, double par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    @Override
    public int getArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        if (!armor.hasTagCompound())
            return this.baseProtection;
        NBTTagCompound armorTag = armor.getTagCompound().getCompoundTag(SET_NAME);
        double amount = armorTag.getDouble("protection") / 4;
        if (amount > 0 && amount < 1)
            amount = 1;
        return (int) (Math.floor(amount));
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        NBTTagCompound tags = stack.getTagCompound();
        NBTTagCompound data;

        if (tags == null)
        {
            tags = new NBTTagCompound();
            stack.setTagCompound(tags);
            data = new NBTTagCompound();
            tags.setCompoundTag(SET_NAME, data);
            data.setDouble("damageReduction", baseProtection);
        }

        data = tags.getCompoundTag(SET_NAME);
        int dmg = data.getInteger("Damage");
        dmg += damage;
        data.setDouble("Damage", dmg);
    }

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

        return tags.getCompoundTag(SET_NAME).getInteger("TotalDurability");
    }

    public int getItemMaxDamageFromStackForDisplay (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }

        return tags.getCompoundTag(SET_NAME).getInteger("Damage");
    }

}