package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.events.TinkerToolEvent;

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
    // check material
    for(String type : stack.getItem().getToolClasses(stack)) {
      if(state.getBlock().isToolEffective(type, state)) {
        return true;
      }
    }

    return false;
  }

  // also checks for the tools effectiveness
  protected static boolean isToolEffective2(ItemStack stack, IBlockState state) {
    if(isToolEffective(stack, state))
      return true;

    if(stack.getItem() instanceof ToolCore && ((ToolCore) stack.getItem()).isEffective(state.getBlock()))
      return true;

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

  /* Harvesting */

  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth) {
    return calcAOEBlocks(stack, world, player, origin, width, height, depth, -1);
  }

  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance) {
    // only works with toolcore because we need the raytrace call
    if(stack == null || !(stack.getItem() instanceof ToolCore))
      return ImmutableList.of();

    // find out where the player is hitting the block
    IBlockState state = world.getBlockState(origin);
    Block block = state.getBlock();

    if(block.getMaterial() == Material.air) {
      // what are you DOING?
      return ImmutableList.of();
    }

    MovingObjectPosition mop = ((ToolCore) stack.getItem()).getMovingObjectPositionFromPlayer(world, player, false);
    if(mop == null) {
      return ImmutableList.of();
    }

    // fire event
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, width, height, depth, distance);
    width = event.width;
    height = event.height;
    depth = event.depth;
    distance = event.distance;

    // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
    int x,y,z;
    BlockPos start = origin;
    switch(mop.sideHit) {
      case DOWN:
      case UP:
        // x y depends on the angle we look?
        Vec3i vec = player.getHorizontalFacing().getDirectionVec();
        x = vec.getX() * height + vec.getZ() * width;
        y = mop.sideHit.getAxisDirection().getOffset() * -depth;
        z = vec.getX() * width + vec.getZ() * height;
        start = start.add(-x/2, 0, -z/2);
        if(x % 2 == 0) {
          if(x > 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() > 0.5d) start = start.add(1,0,0);
          else if (x < 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() < 0.5d) start = start.add(-1,0,0);
        }
        if(z % 2 == 0) {
          if(z > 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() > 0.5d) start = start.add(0,0,1);
          else if(z < 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() < 0.5d) start = start.add(0,0,-1);
        }
        break;
      case NORTH:
      case SOUTH:
        x = width;
        y = height;
        z = mop.sideHit.getAxisDirection().getOffset() * -depth;
        start = start.add(-x/2, -y/2, 0);
        if(x % 2 == 0 && mop.hitVec.xCoord - mop.getBlockPos().getX() > 0.5d) start = start.add(1,0,0);
        if(y % 2 == 0 && mop.hitVec.yCoord - mop.getBlockPos().getY() > 0.5d) start = start.add(0,1,0);
        break;
      case WEST:
      case EAST:
        x = mop.sideHit.getAxisDirection().getOffset() * -depth;
        y = height;
        z = width;
        start = start.add(-0, -y/2, -z/2);
        if(y % 2 == 0 && mop.hitVec.yCoord - mop.getBlockPos().getY() > 0.5d) start = start.add(0,1,0);
        if(z % 2 == 0 && mop.hitVec.zCoord - mop.getBlockPos().getZ() > 0.5d) start = start.add(0,0,1);
        break;
      default:
        x = y = z = 0;
    }

    ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
    for(int xp = start.getX(); xp != start.getX() + x; xp += x/MathHelper.abs_int(x)) {
      for(int yp = start.getY(); yp != start.getY() + y; yp += y/MathHelper.abs_int(y)) {
        for(int zp = start.getZ(); zp != start.getZ() + z; zp += z/MathHelper.abs_int(z)) {
          // don't add the origin block
          if(xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
            continue;
          }
          if(distance > 0 && MathHelper.abs_int(xp - origin.getX()) + MathHelper.abs_int(yp - origin.getY()) + MathHelper.abs_int(zp - origin.getZ()) > distance) {
            continue;
          }
          BlockPos pos = new BlockPos(xp, yp, zp);
          if(isToolEffective2(stack, world.getBlockState(pos))) {
            builder.add(pos);
          }
        }
      }
    }

    return builder.build();
  }

  public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
    // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
    if (world.isAirBlock(pos))
      return;

    // check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
    // or precious ores you can't harvest while mining stone
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    // only effective materials
    if(!isToolEffective2(stack, state)) {
      return;
    }

    IBlockState refState = world.getBlockState(refPos);
    float refStrength = ForgeHooks.blockStrength(refState, player, world, refPos);
    float strength = ForgeHooks.blockStrength(state, player, world, pos);

    // only harvestable blocks that aren't impossibly slow to harvest
    if (!ForgeHooks.canHarvestBlock(block, player, world, pos) || refStrength/strength > 10f)
      return;

    if (player.capabilities.isCreativeMode) {
      block.onBlockHarvested(world, pos, state, player);
      if (block.removedByPlayer(world, pos, player, false))
        block.onBlockDestroyedByPlayer(world, pos, state);

      // send update to client
      if (!world.isRemote) {
        ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(world, pos));
      }
      return;
    }

    // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
    player.getCurrentEquippedItem().onBlockDestroyed(world, block, pos, player);

    // server sided handling
    if (!world.isRemote) {
      // serverside we reproduce ItemInWorldManager.tryHarvestBlock

      // ItemInWorldManager.removeBlock
      block.onBlockHarvested(world, pos, state, player);

      if(block.removedByPlayer(world, pos, player, true)) // boolean is if block can be harvested, checked above
      {
        block.onBlockDestroyedByPlayer(world, pos, state);
        block.harvestBlock(world, player, pos, state, world.getTileEntity(pos));
      }

      // always send block update to client
      EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
      mpPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(world, pos));
    }
    // client sided handling
    else {
      PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
      // clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
      // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

      // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
      world.playAuxSFX(2001, pos, Block.getStateId(state));
      if(block.removedByPlayer(world, pos, player, true))
      {
        block.onBlockDestroyedByPlayer(world, pos, state);
      }
      // callback to the tool
      ItemStack itemstack = player.getCurrentEquippedItem();
      if (itemstack != null)
      {
        itemstack.onBlockDestroyed(world, block, pos, player);

        if (itemstack.stackSize == 0)
        {
          player.destroyCurrentEquippedItem();
        }
      }

      // send an update to the server, so we get an update back
      //if(PHConstruct.extraBlockUpdates)
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
    }
  }

  /* Tool Durability */

  public static int getCurrentDurability(ItemStack stack) {
    return stack.getMaxDamage() - stack.getItemDamage();
  }

  /** Damages the tool. Entity is only needed in case the tool breaks for rendering the break effect. */
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
    actualAmount = Math.min(actualAmount, getCurrentDurability(stack));
    stack.setItemDamage(stack.getItemDamage() + actualAmount);

    if(getCurrentDurability(stack) == 0) {
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

    if(entity != null) {
      entity.renderBrokenItemStack(stack);
    }
  }

  public static void repairTool(ItemStack stack, int amount) {
    // entity is optional, only needed for rendering break effect, never needed when repairing
    repairTool(stack, amount, null);
  }

  public static void repairTool(ItemStack stack, int amount, EntityLivingBase entity) {
    if(isBroken(stack)) {
      NBTTagCompound tag = TagUtil.getToolTag(stack);
      tag.setBoolean(Tags.BROKEN, false);
      TagUtil.setToolTag(stack, tag);

      stack.setItemDamage(stack.getMaxDamage());
    }

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
    if(isBroken(stack)) {
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

    double oldVelX = target.motionX;
    double oldVelY = target.motionY;
    double oldVelZ = target.motionZ;
    // deal the damage
    boolean hit = tool.dealDamage(stack, player, target, damage);

    // did we hit?
    if(hit) {
      // actual damage dealt
      float damageDealt = oldHP - target.getHealth();

      // apply knockback modifier
      oldVelX = target.motionX = oldVelX + (target.motionX - oldVelX)*tool.knockback();
      oldVelY = target.motionY = oldVelY + (target.motionY - oldVelY)*tool.knockback();
      oldVelZ = target.motionZ = oldVelZ + (target.motionZ - oldVelZ)*tool.knockback();

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
      
      // we don't support vanilla thorns or antispider enchantments
      //EnchantmentHelper.applyThornEnchantments(target, player);
      //EnchantmentHelper.applyArthropodEnchantments(player, target);


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

  public static boolean useSecondaryItem(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    int slot = getSecondaryItemSlot(player);

    // last slot selected
    if(slot == player.inventory.currentItem) {
      return false;
    }

    ItemStack secondaryItem = player.inventory.getStackInSlot(slot);

    // do we have an item to use?
    if(secondaryItem == null) {
      return false;
    }

    // use it
    int oldSlot = player.inventory.currentItem;
    player.inventory.currentItem = slot;
    boolean ret = secondaryItem.onItemUse(player, world, pos, side, hitX, hitY, hitZ);
    player.inventory.currentItem = oldSlot;

    return ret;
  }

  public static int getSecondaryItemSlot(EntityPlayer player) {
    int slot = player.inventory.currentItem;
    int max = InventoryPlayer.getHotbarSize() - 1;
    if(slot < max) {
      slot++;
    }

    // find next slot that has an item in it
    for(; slot < max; slot++) {
      ItemStack secondaryItem = player.inventory.getStackInSlot(slot);
      if(secondaryItem != null) {
        if(!(secondaryItem.getItem() instanceof ToolCore) || !((ToolCore) secondaryItem.getItem()).canUseSecondaryItem()) {
          break;
        }
      }
    }

    ItemStack secondaryItem = player.inventory.getStackInSlot(slot);
    if(secondaryItem != null) {
      if((secondaryItem.getItem() instanceof ToolCore) && ((ToolCore) secondaryItem.getItem()).canUseSecondaryItem()) {
        return player.inventory.currentItem;
      }
    }

    return slot;
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
