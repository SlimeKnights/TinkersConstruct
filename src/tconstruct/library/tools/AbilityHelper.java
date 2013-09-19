package tconstruct.library.tools;

import ic2.api.item.ICustomElectricItem;
import ic2.api.item.IElectricItem;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.PiercingEntityDamage;

public class AbilityHelper
{
    public static Random random = new Random();
    public static boolean necroticUHS;

    /* Normal interactions */
    public static boolean onBlockChanged (ItemStack stack, World world, int bID, int x, int y, int z, EntityLivingBase player, Random random)
    {
        if (!stack.hasTagCompound())
            return false;

        int reinforced = 0;
        NBTTagCompound tags = stack.getTagCompound();

        if (tags.getCompoundTag("InfiTool").hasKey("Unbreaking"))
            reinforced = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");

        if (random.nextInt(10) < 10 - reinforced)
        {
            damageTool(stack, 1, tags, player, false);
        }

        return true;
    }

    public static boolean onLeftClickEntity (ItemStack stack, EntityLivingBase player, Entity entity, ToolCore tool)
    {
        return onLeftClickEntity(stack, player, entity, tool, 0);
    }

    public static boolean onLeftClickEntity (ItemStack stack, EntityLivingBase player, Entity entity, ToolCore tool, int baseDamage)
    {
        if (entity.canAttackWithItem() && stack.hasTagCompound())
        {
            if (!entity.hitByEntity(player)) // can't attack this entity
            {
                NBTTagCompound tags = stack.getTagCompound();
                NBTTagCompound toolTags = stack.getTagCompound().getCompoundTag("InfiTool");
                int damage = toolTags.getInteger("Attack") + baseDamage;
                boolean broken = toolTags.getBoolean("Broken");

                int durability = tags.getCompoundTag("InfiTool").getInteger("Damage");
                float stonebound = tags.getCompoundTag("InfiTool").getFloat("Shoddy");

                float stoneboundDamage = (float) Math.log(durability / 72f + 1) * -2 * stonebound;

                int earlyModDamage = 0;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    earlyModDamage = mod.baseAttackDamage(earlyModDamage, damage, tool, tags, toolTags, stack, player, entity);
                }
                damage += earlyModDamage;

                if (player.isPotionActive(Potion.damageBoost))
                {
                    damage += 3 << player.getActivePotionEffect(Potion.damageBoost).getAmplifier();
                }

                if (player.isPotionActive(Potion.weakness))
                {
                    damage -= 2 << player.getActivePotionEffect(Potion.weakness).getAmplifier();
                }

                float knockback = 0;
                float enchantDamage = 0;

                if (entity instanceof EntityLivingBase)
                {
                    enchantDamage = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase) entity);
                    knockback += EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase) entity);
                }

                damage += stoneboundDamage;
                if (damage < 1)
                    damage = 1;

                if (player.isSprinting())
                {
                    knockback++;
                    float lunge = tool.chargeAttack();
                    if (lunge > 1f)
                    {
                        knockback += lunge - 1.0f;
                        damage *= lunge;
                    }
                }

                float modKnockback = 0f;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    modKnockback = mod.knockback(modKnockback, knockback, tool, tags, toolTags, stack, player, entity);
                }
                knockback += modKnockback;

                int modDamage = 0;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    modDamage = mod.attackDamage(modDamage, damage, tool, tags, toolTags, stack, player, entity);
                }
                damage += modDamage;

                if (damage > 0 || enchantDamage > 0)
                {
                    boolean criticalHit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness)
                            && player.ridingEntity == null && entity instanceof EntityLivingBase;

                    for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                    {
                        if (mod.doesCriticalHit(tool, tags, toolTags, stack, player, entity))
                            criticalHit = true;
                    }

                    if (criticalHit)
                    {
                        damage += random.nextInt(damage / 2 + 2);
                    }

                    damage += enchantDamage;

                    if (tool.getDamageModifier() != 1f)
                    {
                        damage *= tool.getDamageModifier();
                    }
                    boolean var6 = false;
                    int fireAspect = EnchantmentHelper.getFireAspectModifier(player);

                    if (entity instanceof EntityLivingBase && fireAspect > 0 && !entity.isBurning())
                    {
                        var6 = true;
                        entity.setFire(1);
                    }

                    if (broken)
                    {
                        if (baseDamage > 0)
                            damage = baseDamage;
                        else
                            damage = 1;
                    }
                    boolean causedDamage = false;
                    if (tool.pierceArmor() && !broken)
                    {
                        if (player instanceof EntityPlayer)
                            causedDamage = entity.attackEntityFrom(causePlayerPiercingDamage((EntityPlayer) player), damage);
                        else
                            causedDamage = entity.attackEntityFrom(causePiercingDamage(player), damage);
                    }
                    else
                    {
                        if (player instanceof EntityPlayer)
                            causedDamage = entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
                        else
                            causedDamage = entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
                    }

                    if (causedDamage)
                    {
                        int reinforced = 0;
                        if (toolTags.hasKey("Unbreaking"))
                            reinforced = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");

                        if (random.nextInt(10) < 10 - reinforced)
                        {
                            damageTool(stack, 1, tags, player, false);
                        }
                        //damageTool(stack, 1, player, false);
                        tool.onEntityDamaged(player.worldObj, player, entity);
                        if (!necroticUHS || (entity instanceof IMob && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0))
                        {
                            int drain = toolTags.getInteger("Necrotic") * 2;
                            if (drain > 0)
                                player.heal(random.nextInt(drain + 1));
                        }

                        if (knockback > 0)
                        {
                            entity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F), 0.1D,
                                    (double) (MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F));
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (player instanceof EntityPlayer)
                        {
                            if (criticalHit)
                            {
                                ((EntityPlayer) player).onCriticalHit(entity);
                            }

                            if (enchantDamage > 0)
                            {
                                ((EntityPlayer) player).onEnchantmentCritical(entity);
                            }

                            if (damage >= 18)
                            {
                                ((EntityPlayer) player).triggerAchievement(AchievementList.overkill);
                            }
                        }

                        player.setLastAttacker(entity);

                        if (entity instanceof EntityLivingBase)
                        {
                            EnchantmentThorns.func_92096_a(player, (EntityLivingBase) entity, random);
                        }
                    }

                    if (entity instanceof EntityLivingBase)
                    {
                        if (entity instanceof EntityPlayer)
                        {
                            stack.hitEntity((EntityLivingBase) entity, (EntityPlayer) player);
                            if (entity.isEntityAlive())
                            {
                                alertPlayerWolves((EntityPlayer) player, (EntityLivingBase) entity, true);
                            }

                            ((EntityPlayer) player).addStat(StatList.damageDealtStat, damage);
                        }
                        else
                        {
                            Item.itemsList[stack.itemID].hitEntity(stack, (EntityLivingBase) entity, player);
                        }

                        if ((fireAspect > 0 || toolTags.hasKey("Fiery") || toolTags.hasKey("Lava")) && causedDamage)
                        {
                            fireAspect *= 4;
                            if (toolTags.hasKey("Fiery"))
                            {
                                fireAspect += toolTags.getInteger("Fiery") / 5 + 1;
                            }
                            if (toolTags.getBoolean("Lava"))
                            {
                                fireAspect += 3;
                            }
                            entity.setFire(fireAspect);
                        }
                        else if (var6)
                        {
                            entity.extinguish();
                        }
                    }

                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) player).addExhaustion(0.3F);
                    if (causedDamage)
                        return true;
                }
            }
        }
        return false;
    }

    static void alertPlayerWolves (EntityPlayer player, EntityLivingBase living, boolean par2)
    {
        if (!(living instanceof EntityCreeper) && !(living instanceof EntityGhast))
        {
            if (living instanceof EntityWolf)
            {
                EntityWolf var3 = (EntityWolf) living;

                if (var3.isTamed() && player.username.equals(var3.getOwnerName()))
                {
                    return;
                }
            }

            if (!(living instanceof EntityPlayer) || player.canAttackPlayer((EntityPlayer) living))
            {
                List var6 = player.worldObj.getEntitiesWithinAABB(EntityWolf.class,
                        AxisAlignedBB.getAABBPool().getAABB(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
                Iterator var4 = var6.iterator();

                while (var4.hasNext())
                {
                    EntityWolf var5 = (EntityWolf) var4.next();

                    if (var5.isTamed() && var5.getEntityToAttack() == null && player.username.equals(var5.getOwnerName()) && (!par2 || !var5.isSitting()))
                    {
                        var5.setSitting(false);
                        var5.setTarget(living);
                    }
                }
            }
        }
    }

    /* Tool specific */
    public static void damageTool (ItemStack stack, int dam, EntityLivingBase entity, boolean ignoreCharge)
    {
        NBTTagCompound tags = stack.getTagCompound();
        damageTool(stack, dam, tags, entity, ignoreCharge);
    }

    public static void healTool (ItemStack stack, int dam, EntityLivingBase entity, boolean ignoreCharge)
    {
        NBTTagCompound tags = stack.getTagCompound();
        damageTool(stack, -dam, tags, entity, ignoreCharge);
    }

    public static void damageTool (ItemStack stack, int dam, NBTTagCompound tags, EntityLivingBase entity, boolean ignoreCharge)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode || tags == null)
            return;

        if (ignoreCharge || !damageElectricTool(stack, tags, entity))
        {
            boolean damagedTool = false;
            for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
            {
                if (mod.damageTool(stack, dam, entity))
                    damagedTool = true;
            }

            if (damagedTool)
                return;

            int damage = tags.getCompoundTag("InfiTool").getInteger("Damage");
            int damageTrue = damage + dam;
            int maxDamage = tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
            if (damageTrue <= 0)
            {
                tags.getCompoundTag("InfiTool").setInteger("Damage", 0);
                stack.setItemDamage(0);
                tags.getCompoundTag("InfiTool").setBoolean("Broken", false);
            }

            else if (damageTrue > maxDamage)
            {
                breakTool(stack, tags, entity);
                stack.setItemDamage(0);
            }

            else
            {
                tags.getCompoundTag("InfiTool").setInteger("Damage", damage + dam);
                int toolDamage = (damage * 100 / maxDamage) + 1;
                int stackDamage = stack.getItemDamage();
                if (toolDamage != stackDamage)
                {
                    stack.setItemDamage((damage * 100 / maxDamage) + 1);
                }
            }
        }
    }

    public static boolean damageElectricTool (ItemStack stack, NBTTagCompound tags, Entity entity)
    {
        if (!tags.hasKey("charge"))
            return false;

        NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
        int charge = tags.getInteger("charge");

        int durability = toolTag.getInteger("Damage");
        float shoddy = toolTag.getFloat("Shoddy");

        float mineSpeed = toolTag.getInteger("MiningSpeed");
        int heads = 1;
        if (toolTag.hasKey("MiningSpeed2"))
        {
            mineSpeed += toolTag.getInteger("MiningSpeed2");
            heads++;
        }

        if (toolTag.hasKey("MiningSpeedHandle"))
        {
            mineSpeed += toolTag.getInteger("MiningSpeedHandle");
            heads++;
        }

        if (toolTag.hasKey("MiningSpeedExtra"))
        {
            mineSpeed += toolTag.getInteger("MiningSpeedExtra");
            heads++;
        }
        float trueSpeed = mineSpeed / (heads * 100f);
        float stonebound = toolTag.getFloat("Shoddy");
        float bonusLog = (float) Math.log(durability / 72f + 1) * 2 * stonebound;
        trueSpeed += bonusLog;
        trueSpeed *= 6;

        if (charge < trueSpeed)
        {
            if (charge > 0)
                tags.setInteger("charge", 0);
            return false;
        }

        charge -= trueSpeed;
        ToolCore tool = (ToolCore) stack.getItem();
        stack.setItemDamage(1 + (tool.getMaxCharge(stack) - charge) * (stack.getMaxDamage() - 1) / tool.getMaxCharge(stack));
        tags.setInteger("charge", charge);
        if (entity instanceof EntityPlayer)
            chargeFromArmor(stack, (EntityPlayer) entity);
        return true;
    }

    static void chargeFromArmor (ItemStack stack, EntityPlayer player)
    {
        boolean inContainer = false;

        for (int armorIter = 0; armorIter < 4; ++armorIter)
        {
            ItemStack armor = player.inventory.armorInventory[armorIter];

            if (armor != null && armor.getItem() instanceof IElectricItem)
            {
                IElectricItem electricArmor = (IElectricItem) armor.getItem();
                ToolCore tool = (ToolCore) stack.getItem();

                if (electricArmor.canProvideEnergy(stack) && electricArmor.getTier(stack) >= ((IElectricItem) stack.getItem()).getTier(stack))
                {
                    int chargeAmount = tool.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
                    chargeAmount = discharge(armor, chargeAmount, Integer.MAX_VALUE, true, false);

                    if (chargeAmount > 0)
                    {
                        tool.charge(stack, chargeAmount, Integer.MAX_VALUE, true, false);
                        inContainer = true;
                    }
                }
            }
        }

        if (inContainer)
        {
            player.openContainer.detectAndSendChanges();
        }
    }

    public static int discharge (ItemStack stack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
    {
        IElectricItem ielectricitem = (IElectricItem) stack.getItem();

        if (ielectricitem instanceof ICustomElectricItem)
        {
            return ((ICustomElectricItem) ielectricitem).discharge(stack, amount, tier, ignoreTransferLimit, simulate);
        }
        else if (amount >= 0 && stack.stackSize <= 1 && ielectricitem.getTier(stack) <= tier)
        {
            if (amount > ielectricitem.getTransferLimit(stack) && !ignoreTransferLimit)
            {
                amount = ielectricitem.getTransferLimit(stack);
            }

            NBTTagCompound tags = stack.getTagCompound();
            int charge = tags.getInteger("charge");

            if (amount > charge)
            {
                amount = charge;
            }

            charge -= amount;

            if (!simulate)
            {
                tags.setInteger("charge", charge);
                stack.itemID = charge > 0 ? ielectricitem.getChargedItemId(stack) : ielectricitem.getEmptyItemId(stack);

                if (stack.getItem() instanceof IElectricItem)
                {
                    ielectricitem = (IElectricItem) stack.getItem();

                    if (stack.getMaxDamage() > 2)
                    {
                        if (stack.getItemDamage() + 1 < stack.getMaxDamage())
                            stack.setItemDamage(1 + (ielectricitem.getMaxCharge(stack) - charge) * (stack.getMaxDamage() - 2) / ielectricitem.getMaxCharge(stack));
                    }
                    else
                    {
                        stack.setItemDamage(0);
                    }
                }
                else
                {
                    stack.setItemDamage(0);
                }
            }

            return amount;
        }
        else
        {
            return 0;
        }

    }

    public static void breakTool (ItemStack stack, NBTTagCompound tags, Entity entity)
    {
        tags.getCompoundTag("InfiTool").setBoolean("Broken", true);
        if (entity != null)
            entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, "random.break", 1f, 1f, true);
    }

    public static void repairTool (ItemStack stack, NBTTagCompound tags)
    {
        tags.getCompoundTag("InfiTool").setBoolean("Broken", false);
        tags.getCompoundTag("InfiTool").setInteger("Damage", 0);
    }

    public static DamageSource causePiercingDamage (EntityLivingBase mob)
    {
        return new PiercingEntityDamage("mob", mob);
    }

    public static DamageSource causePlayerPiercingDamage (EntityPlayer player)
    {
        return new PiercingEntityDamage("player", player);
    }

    public static void knockbackEntity (EntityLivingBase living, double boost)
    {
        living.motionX *= boost;
        //living.motionY *= boost/2;
        living.motionZ *= boost;
    }

    public static boolean hoeGround (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, Random random)
    {
        if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        else
        {
            UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return false;
            }

            if (event.getResult() == Result.ALLOW)
            {
                onBlockChanged(stack, world, 0, x, y, z, player, random);
                return true;
            }

            int bID = world.getBlockId(x, y, z);
            int bIDabove = world.getBlockId(x, y + 1, z);

            if ((side == 0 || bIDabove != 0 || bID != Block.grass.blockID) && bID != Block.dirt.blockID)
            {
                return false;
            }
            else
            {
                Block block = Block.tilledField;
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.getStepSound(),
                        (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

                if (world.isRemote)
                {
                    return true;
                }
                else
                {
                    world.setBlock(x, y, z, block.blockID);
                    onBlockChanged(stack, world, 0, x, y, z, player, random);
                    return true;
                }
            }
        }
    }

    public static void spawnItemAtPlayer (EntityPlayer player, ItemStack stack)
    {
        EntityItem entityitem = new EntityItem(player.worldObj, player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, stack);
        player.worldObj.spawnEntityInWorld(entityitem);
        if (!(player instanceof FakePlayer))
            entityitem.onCollideWithPlayer(player);
        /*if (!player.inventory.addItemStackToInventory(stack))
        {
            if (!player.worldObj.isRemote)
            {
                EntityItem entityitem = new EntityItem(player.worldObj, player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, stack);
                player.worldObj.spawnEntityInWorld(entityitem);
                entityitem.onCollideWithPlayer(player);
            }
        }
        else
        {
            player.worldObj.playSoundAtEntity(player, "random.pop", 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }*/
    }

    /* Ranged weapons */

    public static void forceAddToInv (EntityPlayer entityplayer, ItemStack itemstack, int i, boolean flag)
    {
        ItemStack itemstack1 = entityplayer.inventory.getStackInSlot(i);
        entityplayer.inventory.setInventorySlotContents(i, itemstack);
        if (itemstack1 != null)
        {
            addToInv(entityplayer, itemstack1, flag);
        }
    }

    public static boolean addToInv (EntityPlayer entityplayer, ItemStack itemstack, boolean flag)
    {
        return addToInv(entityplayer, itemstack, entityplayer.inventory.currentItem, flag);
    }

    public static boolean addToInv (EntityPlayer entityplayer, ItemStack itemstack, int i, boolean flag)
    {
        ItemStack itemstack1 = entityplayer.inventory.getStackInSlot(i);
        boolean flag1;
        if (itemstack1 == null)
        {
            entityplayer.inventory.setInventorySlotContents(i, itemstack);
            flag1 = true;
        }
        else
        {
            flag1 = entityplayer.inventory.addItemStackToInventory(itemstack);
        }
        if (flag && !flag1)
        {
            addItemStackToWorld(entityplayer.worldObj, (float) Math.floor(entityplayer.posX), (float) Math.floor(entityplayer.posY), (float) Math.floor(entityplayer.posZ), itemstack);
            return true;
        }
        else
        {
            return flag1;
        }
    }

    public static EntityItem addItemStackToWorld (World world, float f, float f1, float f2, ItemStack itemstack)
    {
        return addItemStackToWorld(world, f, f1, f2, itemstack, false);
    }

    public static EntityItem addItemStackToWorld (World world, float f, float f1, float f2, ItemStack itemstack, boolean flag)
    {
        EntityItem entityitem;
        if (flag)
        {
            entityitem = new EntityItem(world, f, f1, f2, itemstack);
        }
        else
        {
            float f3 = 0.7F;
            float f4 = random.nextFloat() * f3 + (1.0F - f3) * 0.5F;
            float f5 = 1.2F;
            float f6 = random.nextFloat() * f3 + (1.0F - f3) * 0.5F;
            entityitem = new EntityItem(world, f + f4, f1 + f5, f2 + f6, itemstack);
        }
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
        return entityitem;
    }

    public static MovingObjectPosition raytraceFromEntity (World world, Entity player, boolean par3, double range)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + 1.62D - (double) player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = range;
        if (player instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }
}
