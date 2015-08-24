package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;

public final class ToolHelper {

  private ToolHelper() {
  }

  public static boolean hasCategory(ItemStack stack, Category category) {
    if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof TinkersItem)) {
      return false;
    }

    return ((TinkersItem) stack.getItem()).hasCategory(category);
  }

  /* Basic Tool data */
  public static int getDurability(ItemStack stack) {
    return getIntTag(stack, Tags.DURABILITY);
  }

  public static int getHarvestLevel(ItemStack stack) {
    return getIntTag(stack, Tags.HARVESTLEVEL);
  }

  public static float getMiningSpeed(ItemStack stack) {
    return getfloatTag(stack, Tags.MININGSPEED);
  }

  public static float getAttack(ItemStack stack) {
    return getfloatTag(stack, Tags.ATTACK);
  }

  public static int getFreeModifiers(ItemStack stack) {
    return getIntTag(stack, Tags.FREE_MODIFIERS);
  }

  public static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
    if(blockState == null) {
      return 0f;
    }

    if(!stack.hasTagCompound()) {
      return 1f;
    }

    // check if the tool has the correct class and harvest level
    if(!canHarvest(stack, blockState)) {
      return 0f;
    }

    if(isBroken(stack)) {
      return 0.3f;
    }

    // calculate speed depending on stats

    // strength = default 1
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    float strength = stack.getItem().getStrVsBlock(stack, blockState.getBlock());
    float speed = tag.getFloat(Tags.MININGSPEED);

    return strength * speed;
  }

  /**
   * Returns true if the tool is effective for harvesting the given block.
   */
  public static boolean isToolEffective(ItemStack stack, IBlockState state) {
    for(String type : stack.getItem().getToolClasses(stack)) {
      if(state.getBlock().isToolEffective(type, state)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if an item has the right harvest level of the correct type for the block.
   */
  public static boolean canHarvest(ItemStack stack, IBlockState state) {
    Block block = state.getBlock();

    // doesn't require a tool
    if(block.getMaterial().isToolNotRequired()) {
      return true;
    }

    String type = block.getHarvestTool(state);
    int level = block.getHarvestLevel(state);

    return stack.getItem().getHarvestLevel(stack, type) >= level;
  }



  /* Tool Durability */

  public static void damageTool(ItemStack stack, int amount, EntityLivingBase entity) {
    if(amount == 0 || isBroken(stack))
      return;

    int actualAmount = amount;
    NBTTagList list = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < list.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
      if(trait != null) {
        if(amount > 0) {
          actualAmount = trait.onToolDamage(stack, amount, actualAmount, entity);
        } else {
          actualAmount = trait.onToolHeal(stack, amount, actualAmount, entity);
        }
      }
    }

    // ensure we never deal more damage than durability
    actualAmount = Math.min(actualAmount, stack.getMaxDamage() - stack.getItemDamage());
    stack.damageItem(actualAmount, entity);

    if(stack.getMaxDamage() - stack.getItemDamage() == 0) {
      breakTool(stack, entity);
    }
  }

  public static void healTool(ItemStack stack, int amount, EntityLivingBase entity) {
    damageTool(stack, -amount, entity);
  }

  public static boolean isBroken(ItemStack stack) {
    return TagUtil.getToolTag(stack).getBoolean(Tags.BROKEN);
  }

  public static void breakTool(ItemStack stack, EntityLivingBase entity) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    tag.setBoolean(Tags.BROKEN, true);
    TagUtil.setToolTag(stack, tag);

    stack.setItemDamage(stack.getMaxDamage());

    entity.renderBrokenItemStack(stack);
  }

  public static void repairTool(ItemStack stack, int amount, EntityLivingBase entity) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    tag.setBoolean(Tags.BROKEN, false);
    TagUtil.setToolTag(stack, tag);

    stack.setItemDamage(stack.getMaxDamage());

    healTool(stack, amount, entity);
  }


  /* Dealing tons of damage */

  /**
   * Makes all the calls to attack an entity. Takes enchantments and potions and traits into account. Basically call this when a tool deals damage.
   * Most of this function is the same as {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity targetEntity)}
   */
  public static boolean attackEntity(ItemStack stack, ToolCore tool, EntityPlayer player, Entity targetEntity) {
    // todo: check how 1.9 does this and if we should steal it
    // nothing to do, no target?
    if(targetEntity == null || !targetEntity.canAttackWithItem() || targetEntity.hitByEntity(player) || !stack.hasTagCompound()) {
      return false;
    }
    if(!(targetEntity instanceof EntityLivingBase)) {
      return false;
    }
    EntityLivingBase target = (EntityLivingBase) targetEntity;

    // traits on the tool
    List<ITrait> traits = Lists.newLinkedList();
    NBTTagList traitsTagList = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < traitsTagList.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(traitsTagList.getStringTagAt(i));
      if(trait != null) {
        traits.add(trait);
      }
    }

    // players base damage
    float baseDamage = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

    // missing because not supported by tcon tools: vanilla damage enchantments, we have our own modifiers
    // missing because not supported by tcon tools: vanilla knockback enchantments, we have our own modifiers
    float baseKnockback = player.isSprinting() ? 1 : 0;

    // tool damage
    baseDamage += ToolHelper.getAttack(stack);
    baseDamage *= tool.damagePotential();

    // calculate if it's a critical hit
    boolean isCritical = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && targetEntity instanceof EntityLivingBase;
    for(ITrait trait : traits) {
      if(trait.isCriticalHit(stack, player, target))
        isCritical = true;
    }

    // calculate actual damage
    float damage = baseDamage;
    for(ITrait trait : traits) {
      damage = trait.onHit(stack, player, target, baseDamage, damage, isCritical);
    }

    // apply critical damage
    if(isCritical) {
      damage *= 1.5f;
    }

    // calculate cutoff
    damage = calcCutoffDamage(damage, tool.damageCutoff());

    // calculate actual knockback
    float knockback = baseKnockback;
    for(ITrait trait : traits) {
      knockback = trait.knockBack(stack, player, target, damage, baseKnockback, knockback, isCritical);
    }

    // missing because not supported by tcon tools: vanilla fire aspect enchantments, we have our own modifiers

    float oldHP = target.getHealth();
    // deal the damage
    boolean hit = tool.dealDamage(stack, player, target, damage);

    // did we hit?
    if(hit) {
      // actual damage dealt
      float damageDealt = oldHP - target.getHealth();

      double oldVelX = target.motionX;
      double oldVelY = target.motionY;
      double oldVelZ = target.motionZ;

      // apply knockback
      if(knockback > 0f) {
        double velX = -MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
        double velZ = MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F;
        targetEntity.addVelocity(velX, 0.1d, velZ);

        // slow down player
        player.motionX *= 0.6f;
        player.motionZ *= 0.6f;
        player.setSprinting(false);
      }

      // Send movement changes caused by attacking directly to hit players.
      // I guess this is to allow better handling at the hit players side? No idea why it resets the motion though.
      if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged)
      {
        ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
        targetEntity.velocityChanged = false;
        targetEntity.motionX = oldVelX;
        targetEntity.motionY = oldVelY;
        targetEntity.motionZ = oldVelZ;
      }

      // vanilla critical callback
      if(isCritical) {
        player.onCriticalHit(target);
      }

      // "magical" critical damage? (aka caused by modifiers)
      if(damage > baseDamage) {
        // this usually only displays some particles :)
        player.onEnchantmentCritical(targetEntity);
      }

      // vanilla achievement support :D
      if(damage >= 18f) {
        player.triggerAchievement(AchievementList.overkill);
      }

      player.setLastAttacker(target);

      // no idea what this actually does
      EnchantmentHelper.func_151384_a(target, player);
      EnchantmentHelper.func_151385_b(player, target);


      // call post-hit callbacks before reducing the durability
      for(ITrait trait : traits) {
        trait.afterHit(stack, player, target, damageDealt, isCritical, hit);
      }

      // damage the tool
      stack.hitEntity(target, player);
      damageTool(stack, Math.max(1, (int) damage), player);

      player.addStat(StatList.damageDealtStat, Math.round(damage*10f));
      player.addExhaustion(0.3f);
    }

    return true;
  }

  public static float calcCutoffDamage(float damage, float cutoff) {
    float p = 1f;
    float d = damage;
    damage = 0f;
    while(d > cutoff) {
      damage += p * cutoff;
      // safety for ridiculous values
      if(p > 0.000001f) {
        p *= 0.9f;
      }
      d -= cutoff;
    }

    damage += p*d;

    return damage;
  }

  public static float getActualDamage(ItemStack stack, EntityPlayer player) {
    float damage = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

    if(stack.getItem() instanceof ToolCore) {
      damage += ToolHelper.getAttack(stack);
      damage *= ((ToolCore) stack.getItem()).damagePotential();
      damage = ToolHelper.calcCutoffDamage(damage, ((ToolCore) stack.getItem()).damageCutoff());
    }

    return damage;
  }

  public static void swingItem(int speed, EntityLivingBase entity) {
    if (!entity.isSwingInProgress || entity.swingProgressInt >= 3 || entity.swingProgressInt < 0)
    {
      entity.swingProgressInt = Math.min(4, -1 + speed);
      entity.isSwingInProgress = true;

      if (entity.worldObj instanceof WorldServer)
      {
        ((WorldServer)entity.worldObj).getEntityTracker().sendToAllTrackingEntity(entity, new S0BPacketAnimation(entity, 0));
      }
    }
  }

  /* Helper Functions */

  public static int getIntTag(ItemStack stack, String key) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);

    return tag.getInteger(key);
  }

  public static float getfloatTag(ItemStack stack, String key) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);

    return tag.getFloat(key);
  }
}
