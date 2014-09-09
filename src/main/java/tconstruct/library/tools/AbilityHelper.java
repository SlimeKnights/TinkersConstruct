package tconstruct.library.tools;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.Event.Result;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import tconstruct.library.*;
import tconstruct.library.util.PiercingEntityDamage;

public class AbilityHelper
{
    public static Random random = new Random();
    public static boolean necroticUHS;

    /* Normal interactions */
    public static boolean onBlockChanged (ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase player, Random random)
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
                    boolean criticalHit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && entity instanceof EntityLivingBase;

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
                        // damageTool(stack, 1, player, false);
                        tool.onEntityDamaged(player.worldObj, player, entity);
                        if (!necroticUHS || (entity instanceof IMob && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0))
                        {
                            int drain = toolTags.getInteger("Necrotic") * 2;
                            if (drain > 0)
                                player.heal(random.nextInt(drain + 1));
                        }

                        if (knockback > 0)
                        {
                            entity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F), 0.1D, (double) (MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F));
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
                            DamageSource.causeThornsDamage(entity);// (((EntityLivingBase)player,
                                                                   // (EntityLivingBase)
                                                                   // entity);
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
                            stack.getItem().hitEntity(stack, (EntityLivingBase) entity, player);
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

                if (var3.isTamed() && player.getDisplayName().equals(var3.func_152113_b()))
                {
                    return;
                }
            }

            if (!(living instanceof EntityPlayer) || player.canAttackPlayer((EntityPlayer) living))
            {
                List var6 = player.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
                Iterator var4 = var6.iterator();

                while (var4.hasNext())
                {
                    EntityWolf var5 = (EntityWolf) var4.next();

                    if (var5.isTamed() && var5.getEntityToAttack() == null && player.getDisplayName().equals(var5.func_152113_b()) && (!par2 || !var5.isSitting()))
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

        if (ignoreCharge || !damageEnergyTool(stack, tags, entity))
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

    public static boolean damageEnergyTool (ItemStack stack, NBTTagCompound tags, Entity entity)
    {
        if (!tags.hasKey("Energy"))
            return false;

        NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
        int energy = tags.getInteger("Energy");
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
        if (energy != -1)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            // first try charging from the hotbar
            if (entity instanceof EntityPlayer)
            {
                // workaround for charging flux-capacitors making tools unusable
                chargeEnergyFromHotbar(stack, (EntityPlayer) entity, tags);
                energy = tool.getEnergyStored(stack);
            }

            if (energy < trueSpeed * 2)
            {
                if (energy > 0)
                    tags.setInteger("Energy", 0);
                return false;
            }

            energy -= trueSpeed * 2;
            tags.setInteger("Energy", energy);

            stack.setItemDamage(1 + (tool.getMaxEnergyStored(stack) - energy) * (stack.getMaxDamage() - 1) / tool.getMaxEnergyStored(stack));
        }
        return true;
    }

    protected static void chargeEnergyFromHotbar (ItemStack stack, EntityPlayer player, NBTTagCompound tags)
    {
        // cool kids only
        if (!(stack.getItem() instanceof ToolCore))
            return;

        ToolCore tool = (ToolCore) stack.getItem();

        // check if the tool can actually receive energy
        if (tool.receiveEnergy(stack, 1, true) != 1)
            // no you're not going to charge that potato battery on your tool
            return;

        int buffer = tool.getEnergyStored(stack);
        int max = tool.getMaxEnergyStored(stack);
        int missing = max - buffer;

        // you're full. xbox go home.
        if (missing <= 0)
            return;

        // iterate through hotbar
        for (int iter = 0; iter < 9; ++iter)
        {
            ItemStack slot = player.inventory.mainInventory[iter];

            // check if item is not another tool
            if (slot == null || slot.getItem() instanceof ToolCore)
                continue;
            // check if item gives energy
            if (!(slot.getItem() instanceof IEnergyContainerItem))
                continue;

            IEnergyContainerItem fluxItem = (IEnergyContainerItem) slot.getItem();

            // mDiyo EATS your power
            while (fluxItem.extractEnergy(slot, missing, true) > 0)
            {
                missing -= fluxItem.extractEnergy(slot, missing, false);
            }
        }
        // update energy
        tags.setInteger("Energy", max - missing);
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
        // living.motionY *= boost/2;
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
                damageTool(stack, 1, player, false);
                return true;
            }

            Block block = world.getBlock(x, y, z);

            if (side != 0 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && (block == Blocks.grass || block == Blocks.dirt))
            {
                Block block1 = Blocks.farmland;
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);

                if (world.isRemote)
                {
                    return true;
                }
                else
                {
                    world.setBlock(x, y, z, block1);
                    damageTool(stack, 1, player, false);
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }

    public static void spawnItemAtEntity (Entity entity, ItemStack stack, int delay)
    {
        if (!entity.worldObj.isRemote)
        {
            EntityItem entityitem = new EntityItem(entity.worldObj, entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, stack);
            entityitem.delayBeforeCanPickup = delay;
            entity.worldObj.spawnEntityInWorld(entityitem);
        }
    }

    public static void spawnItemAtPlayer (EntityPlayer player, ItemStack stack)
    {
        if (!player.worldObj.isRemote)
        {
            EntityItem entityitem = new EntityItem(player.worldObj, player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, stack);
            player.worldObj.spawnEntityInWorld(entityitem);
            if (!(player instanceof FakePlayer))
                entityitem.onCollideWithPlayer(player);
        }

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
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
        if (!world.isRemote && player instanceof EntityPlayer)
            d1 += 1.62D;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
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
        return world.func_147447_a(vec3, vec31, par3, !par3, par3);
    }

    public static float calcToolSpeed (ToolCore tool, NBTTagCompound tags)
    {
        float mineSpeed = tags.getInteger("MiningSpeed");
        int heads = 1;
        if (tags.hasKey("MiningSpeed2"))
        {
            mineSpeed += tags.getInteger("MiningSpeed2");
            heads++;
        }

        if (tags.hasKey("MiningSpeedHandle"))
        {
            mineSpeed += tags.getInteger("MiningSpeedHandle");
            heads++;
        }

        if (tags.hasKey("MiningSpeedExtra"))
        {
            mineSpeed += tags.getInteger("MiningSpeedExtra");
            heads++;
        }
        float speedMod = 1f;
        if (tool instanceof HarvestTool)
            speedMod = ((HarvestTool) tool).breakSpeedModifier();

        float trueSpeed = mineSpeed / (heads * 100f) * speedMod;
        trueSpeed += calcStoneboundBonus(tool, tags);

        return trueSpeed;
    }

    public static float calcStoneboundBonus (ToolCore tool, NBTTagCompound tags)
    {
        int durability = tags.getInteger("Damage");
        float stonebound = tags.getFloat("Shoddy");
        float stoneboundMod = 72f;
        if (tool instanceof HarvestTool)
            stoneboundMod = ((HarvestTool) tool).stoneboundModifier();

        return (float) Math.log(durability / stoneboundMod + 1) * 2 * stonebound;
    }
}
