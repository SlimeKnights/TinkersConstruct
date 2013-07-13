package mods.tinker.tconstruct.entity;

import java.lang.ref.WeakReference;
import java.util.Random;

import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GolemBase extends EntityCreature implements IInventory
{
    public WeakReference<EntityLiving> leader; //Monster
    public String ownerName = ""; //Player
    public int maxHealth = 20;
    public int baseAttack;
    public boolean paused;
    int useTime;
    protected static Random rand = new Random();
    //public static CoordTuple target;
    public int targetX;
    public int targetY;
    public int targetZ;
    public boolean targetLock;

    public ItemStack[] inventory;

    public GolemBase(World world)
    {
        super(world);
        setupInventory();
    }

    public void setupInventory ()
    {
        inventory = new ItemStack[0];
    }

    @Override
    public int getMaxHealth ()
    {
        //Workaround for dying on spawn
        if (maxHealth == 0)
            return 20;

        return maxHealth;
    }

    @Override
    public void initCreature ()
    {
        baseAttack = 3;
        paused = false;
    }

    public EntityLiving getOwner ()
    {
        if (leader == null || leader.get() == null)
            return this.worldObj.getPlayerEntityByName(ownerName);
        return leader.get();
    }

    public void setOwner (EntityLivingBase living)
    {
        if (living instanceof EntityPlayer)
            ownerName = ((EntityPlayer) living).username;
        else
            leader = new WeakReference(living);
    }

    public boolean isOwner (Entity entity)
    {
        if (entity == null)
        {
            return false;
        }
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            return entityplayer.username.equalsIgnoreCase(ownerName);
        }
        return false;
    }

    public float getSpeed ()
    {
        return 0.25f;
    }

    /* AI */

    @Override
    protected boolean isAIEnabled ()
    {
        return true;
    }

    protected void updateWanderPath ()
    {
        if (!paused)
            super.updateWanderPath();
    }
    
    public boolean standby()
    {
        return false;
    }

    public boolean following ()
    {
        return false;
    }

    public boolean patrolling ()
    {
        return true;
    }

    public void attackEntityAsGolem (Entity target)
    {
        ItemStack stack = getHeldItem();
        if (stack == null)
            target.attackEntityFrom(DamageSource.causeMobDamage(this), baseAttack);

        else if (stack.getItem() instanceof ToolCore)
        {
            if (stack.hasTagCompound())
            {
                AbilityHelper.onLeftClickEntity(stack, this, target, (ToolCore) stack.getItem(), baseAttack);
            }
            else
                target.attackEntityFrom(DamageSource.causeMobDamage(this), baseAttack);
        }
        else
        {
            if (target.canAttackWithItem())
            {
                if (!target.func_85031_j(this))
                {
                    int i = stack.getDamageVsEntity(target) + baseAttack;

                    if (this.isPotionActive(Potion.damageBoost))
                    {
                        i += 3 << this.getActivePotionEffect(Potion.damageBoost).getAmplifier();
                    }

                    if (this.isPotionActive(Potion.weakness))
                    {
                        i -= 2 << this.getActivePotionEffect(Potion.weakness).getAmplifier();
                    }

                    int j = 0;
                    int k = 0;

                    if (target instanceof EntityLiving)
                    {
                        k = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLiving) target);
                        j += EnchantmentHelper.getKnockbackModifier(this, (EntityLiving) target);
                    }

                    if (this.isSprinting())
                    {
                        ++j;
                    }

                    if (i > 0 || k > 0)
                    {
                        boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null
                                && target instanceof EntityLiving;

                        if (flag && i > 0)
                        {
                            i += this.rand.nextInt(i / 2 + 2);
                        }

                        i += k;
                        boolean flag1 = false;
                        int l = EnchantmentHelper.getFireAspectModifier(this);

                        if (target instanceof EntityLiving && l > 0 && !target.isBurning())
                        {
                            flag1 = true;
                            target.setFire(1);
                        }

                        boolean flag2 = target.attackEntityFrom(DamageSource.causeMobDamage(this), i);

                        if (flag2)
                        {
                            if (j > 0)
                            {
                                target.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) j * 0.5F), 0.1D,
                                        (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) j * 0.5F));
                                this.motionX *= 0.6D;
                                this.motionZ *= 0.6D;
                                this.setSprinting(false);
                            }

                            /*if (flag)
                            {
                                this.onCriticalHit(target);
                            }

                            if (k > 0)
                            {
                                this.onEnchantmentCritical(target);
                            }

                            if (i >= 18)
                            {
                                this.triggerAchievement(AchievementList.overkill);
                            }*/

                            this.setLastAttackingEntity(target);

                            if (target instanceof EntityLiving)
                            {
                                EnchantmentThorns.func_92096_a(this, (EntityLiving) target, this.rand);
                            }
                        }

                        //ItemStack stack = this.getCurrentEquippedItem();
                        Object object = target;

                        if (target instanceof EntityDragonPart)
                        {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart) target).entityDragonObj;

                            if (ientitymultipart != null && ientitymultipart instanceof EntityLiving)
                            {
                                object = (EntityLiving) ientitymultipart;
                            }
                        }

                        if (stack != null && object instanceof EntityLiving)
                        {
                            //stack.hitEntity((EntityLiving)object, this);
                            Item.itemsList[stack.itemID].hitEntity(stack, (EntityLiving) object, this);

                            if (stack.stackSize <= 0)
                            {
                                this.destroyCurrentEquippedItem();
                            }
                        }

                        if (target instanceof EntityLiving)
                        {
                            /*if (target.isEntityAlive())
                            {
                                this.alertWolves((EntityLiving)target, true);
                            }

                            this.addStat(StatList.damageDealtStat, i);*/

                            if (l > 0 && flag2)
                            {
                                target.setFire(l * 4);
                            }
                            else if (flag1)
                            {
                                target.extinguish();
                            }
                        }

                        //this.addExhaustion(0.3F);
                    }
                }
            }
        }
    }

    public void teleport (double x, double y, double z)
    {
        this.setPosition(x, y, z);
        worldObj.playSoundAtEntity(this, "mob.endermen.portal", 0.5F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
    }

    /* Other */
    protected boolean canDespawn ()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getItemIcon (ItemStack par1ItemStack, int par2)
    {
        Icon icon = super.getItemIcon(par1ItemStack, par2);
        if (par1ItemStack.getItem().requiresMultipleRenderPasses())
        {
            return par1ItemStack.getItem().getIcon(par1ItemStack, par2);
        }

        return icon;
    }

    /* Inventory */
    @Override
    public ItemStack getStackInSlot (int slot)
    {
        if (slot < 0 || slot >= inventory.length)
            return null;
        return inventory[slot];
    }

    public boolean isStackInSlot (int slot)
    {
        if (slot < 0 || slot >= inventory.length)
            return false;
        return inventory[slot] != null;
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getInvName ()
    {
        return "golem.none";
    }

    @Override
    public boolean isInvNameLocalized ()
    {
        return false;
    }

    @Override
    public void onInventoryChanged ()
    {

    }

    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    public void destroyCurrentEquippedItem ()
    {
        worldObj.playSoundAtEntity(this, "random.break", 0.5F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
        this.setCurrentItemOrArmor(0, null);
    }

    /* Saving */
    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        tags.setString("OwnerName", ownerName);
        NBTTagList nbttaglist = new NBTTagList();
        for (int iter = 0; iter < inventory.length; iter++)
        {
            if (inventory[iter] != null)
            {
                NBTTagCompound tagList = new NBTTagCompound();
                tagList.setByte("Slot", (byte) iter);
                inventory[iter].writeToNBT(tagList);
                nbttaglist.appendTag(tagList);
            }
        }

        tags.setTag("Items", nbttaglist);

    }

    public void readEntityFromNBT (NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        ownerName = tags.getString("OwnerName");
        NBTTagList nbttaglist = tags.getTagList("Items");
        inventory = new ItemStack[getSizeInventory()];
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
            byte slotID = tagList.getByte("Slot");
            if (slotID >= 0 && slotID < inventory.length)
            {
                inventory[slotID] = ItemStack.loadItemStackFromNBT(tagList);
            }
        }
    }
}
