package slimeknights.tconstruct.library.entity;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.events.ProjectileEvent;
import slimeknights.tconstruct.library.events.TinkerProjectileImpactEvent;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.ILauncher;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.utils.AmmoHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.ToolHelper;

// have to base this on EntityArrow, otherwise minecraft does derp things because everything is handled based on class.
public abstract class EntityProjectileBase extends EntityArrow implements IEntityAdditionalSpawnData {

  protected static final UUID PROJECTILE_POWER_MODIFIER = UUID.fromString("c6aefc21-081a-4c4a-b076-8f9d6cef9122");
  // projectiles tend to land about this far from any given block face
  private static final AxisAlignedBB ON_BLOCK_AABB = new AxisAlignedBB(-0.05D, -0.05D, -0.05D, 0.05D, 0.05D, 0.05D);

  public TinkerProjectileHandler tinkerProjectile = new TinkerProjectileHandler();

  public boolean bounceOnNoDamage = true;
  public boolean defused = false; // if this is true it wont hit any entities anymore

  public EntityProjectileBase(World world) {
    super(world);

    init();
  }

  public EntityProjectileBase(World world, double d, double d1, double d2) {
    this(world);
    this.setPosition(d, d1, d2);
  }

  public EntityProjectileBase(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
    this(world);

    this.shootingEntity = player;

    pickupStatus = player.isCreative() ? PickupStatus.CREATIVE_ONLY : PickupStatus.ALLOWED;

    // stuff from the arrow
    this.setLocationAndAngles(player.posX, player.posY + player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);

    this.setPosition(this.posX, this.posY, this.posZ);
    //this.yOffset = 0.0F;
    this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
    this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
    this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
    this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, inaccuracy);

    // our stuff
    tinkerProjectile.setItemStack(stack);
    tinkerProjectile.setLaunchingStack(launchingStack);
    tinkerProjectile.setPower(power);

    for(IProjectileTrait trait : tinkerProjectile.getProjectileTraits()) {
      trait.onLaunch(this, world, player);
    }
  }

  protected void init() {

  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return capability == CapabilityTinkerProjectile.PROJECTILE_CAPABILITY || super.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if(capability == CapabilityTinkerProjectile.PROJECTILE_CAPABILITY) {
      return (T) tinkerProjectile;
    }
    return super.getCapability(capability, facing);
  }

  public boolean isDefused() {
    return defused;
  }

  protected void defuse() {
    this.defused = true;
  }

  @Nonnull
  @Override
  protected ItemStack getArrowStack() {
    return tinkerProjectile.getItemStack();
  }

  protected void playHitBlockSound(float speed, IBlockState state) {
    Material material = state.getMaterial();

    if(material == Material.WOOD) {
      this.playSound(Sounds.wood_hit, 1f, 1f);
    }
    else if(material == Material.ROCK) {
      this.playSound(Sounds.stone_hit, 1f, 1f);
    }

    this.playSound(state.getBlock().getSoundType().getStepSound(), 0.8f, 1.0f);
  }

  protected void playHitEntitySound() {
    this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
  }

  /**
   * How deep the item enters stuff it hits. Best experiment.
   */
  public double getStuckDepth() {
    return 0.4f;
  }

  protected void onEntityHit(Entity entityHit) {
    setDead();
  }

  protected float getSpeed() {
    return MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
  }

  public void onHitBlock(RayTraceResult raytraceResult) {
    BlockPos blockpos = raytraceResult.getBlockPos();
    this.xTile = blockpos.getX();
    this.yTile = blockpos.getY();
    this.zTile = blockpos.getZ();
    IBlockState iblockstate = this.getEntityWorld().getBlockState(blockpos);
    this.inTile = iblockstate.getBlock();
    this.inData = this.inTile.getMetaFromState(iblockstate);
    this.motionX = ((float) (raytraceResult.hitVec.x - this.posX));
    this.motionY = ((float) (raytraceResult.hitVec.y - this.posY));
    this.motionZ = ((float) (raytraceResult.hitVec.z - this.posZ));
    float speed = getSpeed();
    this.posX -= this.motionX / speed * 0.05000000074505806D;
    this.posY -= this.motionY / speed * 0.05000000074505806D;
    this.posZ -= this.motionZ / speed * 0.05000000074505806D;

    playHitBlockSound(speed, iblockstate);

    ProjectileEvent.OnHitBlock.fireEvent(this, speed, blockpos, iblockstate);

    this.inGround = true;
    this.arrowShake = 7;
    this.setIsCritical(false);

    if(iblockstate.getMaterial() != Material.AIR) {
      this.inTile.onEntityCollidedWithBlock(this.getEntityWorld(), blockpos, iblockstate, this);
    }

    defuse(); // defuse it so it doesn't hit stuff anymore, being weird
  }

  public void onHitEntity(RayTraceResult raytraceResult) {
    ItemStack item = tinkerProjectile.getItemStack();
    ItemStack launcher = tinkerProjectile.getLaunchingStack();
    boolean bounceOff = false;
    Entity entityHit = raytraceResult.entityHit;
    // deal damage if we have everything
    if(item.getItem() instanceof ToolCore && this.shootingEntity instanceof EntityLivingBase) {
      EntityLivingBase attacker = (EntityLivingBase) this.shootingEntity;
      //EntityLivingBase target = (EntityLivingBase) raytraceResult.entityHit;

      // find the actual itemstack in the players inventory
      ItemStack inventoryItem = AmmoHelper.getMatchingItemstackFromInventory(tinkerProjectile.getItemStack(), attacker, false);
      if(inventoryItem.isEmpty() || inventoryItem.getItem() != item.getItem()) {
        // backup, use saved itemstack
        inventoryItem = item;
      }

      // for the sake of dealing damage we always ensure that the impact itemstack has the correct broken state
      // since the ammo stack can break while the arrow travels/if it's the last arrow
      boolean brokenStateDiffers = ToolHelper.isBroken(inventoryItem) != ToolHelper.isBroken(item);
      if(brokenStateDiffers) {
        toggleBroken(inventoryItem);
      }

      Multimap<String, AttributeModifier> projectileAttributes = null;
      // remove stats from held items
      if(!getEntityWorld().isRemote) {
        unequip(attacker, EntityEquipmentSlot.OFFHAND);
        unequip(attacker, EntityEquipmentSlot.MAINHAND);

        // apply stats from projectile
        if(item.getItem() instanceof IProjectile) {
          projectileAttributes = ((IProjectile) item.getItem()).getProjectileAttributeModifier(inventoryItem);

          if(launcher.getItem() instanceof ILauncher) {
            ((ILauncher) launcher.getItem()).modifyProjectileAttributes(projectileAttributes, tinkerProjectile.getLaunchingStack(), tinkerProjectile.getItemStack(), tinkerProjectile.getPower());
          }

          // factor in power
          projectileAttributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                                   new AttributeModifier(PROJECTILE_POWER_MODIFIER, "Weapon damage multiplier", tinkerProjectile.getPower() - 1f, 2));

          attacker.getAttributeMap().applyAttributeModifiers(projectileAttributes);
        }
        // deal the damage
        float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        bounceOff = !dealDamage(speed, inventoryItem, attacker, entityHit);
        if(!bounceOff) {
          for(IProjectileTrait trait : tinkerProjectile.getProjectileTraits()) {
            trait.afterHit(this, getEntityWorld(), inventoryItem, attacker, entityHit, speed);
          }
        }
        if(brokenStateDiffers) {
          toggleBroken(inventoryItem);
        }

        // remove stats from projectile
        // apply stats from projectile
        if(item.getItem() instanceof IProjectile) {
          assert projectileAttributes != null;
          attacker.getAttributeMap().removeAttributeModifiers(projectileAttributes);
        }

        // readd stats from held items
        equip(attacker, EntityEquipmentSlot.MAINHAND);
        equip(attacker, EntityEquipmentSlot.OFFHAND);
      }

      if(!bounceOff) {
        onEntityHit(entityHit);
      }
    }

    if(bounceOff) {
      if(!bounceOnNoDamage) {
        this.setDead();
      }

      // bounce off if we didn't deal damage
      this.motionX *= -0.10000000149011612D;
      this.motionY *= -0.10000000149011612D;
      this.motionZ *= -0.10000000149011612D;
      this.rotationYaw += 180.0F;
      this.prevRotationYaw += 180.0F;
      this.ticksInAir = 0;

      // 1.9
      /*
      if (!this.worldObj.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D)
      {
        if (this.canBePickedUp == EntityArrow.PickupStatus.ALLOWED)
        {
          this.entityDropItem(this.getArrowStack(), 0.1F);
        }
      
        this.setDead();
      }*/
    }

    playHitEntitySound();
  }

  private void unequip(EntityLivingBase entity, EntityEquipmentSlot slot) {
    ItemStack stack = entity.getItemStackFromSlot(slot);
    if(!stack.isEmpty()) {
      entity.getAttributeMap().removeAttributeModifiers(stack.getAttributeModifiers(slot));
    }
  }

  private void equip(EntityLivingBase entity, EntityEquipmentSlot slot) {
    ItemStack stack = entity.getItemStackFromSlot(slot);
    if(!stack.isEmpty()) {
      entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
    }
  }

  private void toggleBroken(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    tag.setBoolean(Tags.BROKEN, !tag.getBoolean(Tags.BROKEN));
    TagUtil.setToolTag(stack, tag);
  }

  // returns true if it was successful
  public boolean dealDamage(float speed, ItemStack item, EntityLivingBase attacker, Entity target) {
    return ToolHelper.attackEntity(item, (ToolCore) item.getItem(), attacker, target, this);
  }

  @Override
  public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
    // don't do anything, we set it ourselves at spawn
    // Mojangs code has a hard cap of 3.9 speed, but our projectiles can go faster, which desyncs client and server speeds
    // Packet that's causing it: S12PacketEntityVelocity
  }

  @Override
  // this function is the same as the vanilla EntityArrow
  public void onUpdate() {
    // call the entity update routine
    // luckily we can call this directly and take the arrow-code, since we'd have to call super.onUpdate otherwise. Which would not work.
    onEntityUpdate();

    for(IProjectileTrait trait : tinkerProjectile.getProjectileTraits()) {
      trait.onProjectileUpdate(this, getEntityWorld(), tinkerProjectile.getItemStack());
    }

    // boioioiooioing
    if(this.arrowShake > 0) {
      --this.arrowShake;
    }

    // If we don't have our rotation set correctly, infer it from our motion direction
    if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI);
    }

    // we previously hit something. Check if the block is still there.
    BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
    IBlockState iblockstate = this.getEntityWorld().getBlockState(blockpos);

    if(iblockstate.getMaterial() != Material.AIR) {
      AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.getEntityWorld(), blockpos);

      assert axisalignedbb != null;
      if(axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
        this.inGround = true;
      }
    }

    if(this.inGround) {
      updateInGround(iblockstate);
    }
    else {
      updateInAir();
    }
  }

  // Update while we're stuck in a block
  public void updateInGround(IBlockState state) {
    Block block = state.getBlock();
    int meta = block.getMetaFromState(state);

    // check if it's still the same block or if it is already within tolerance of another hitbox
    // second part prevents it from falling when the block changes but the hitbox does nots
    if((block == this.inTile && meta == this.inData) || this.getEntityWorld().collidesWithAnyBlock(ON_BLOCK_AABB.offset(this.getPositionVector()))) {
      ++this.ticksInGround;

      if(this.ticksInGround >= 1200) {
        this.setDead();
      }
    }
    else {
      this.inGround = false;
      this.motionX *= this.rand.nextFloat() * 0.2F;
      this.motionY *= this.rand.nextFloat() * 0.2F;
      this.motionZ *= this.rand.nextFloat() * 0.2F;
      this.ticksInGround = 0;
      this.ticksInAir = 0;
    }

    ++this.timeInGround;
  }

  // update while traveling
  public void updateInAir() {
    // tick tock
    this.timeInGround = 0;
    ++this.ticksInAir;

    // do a raytrace from old to new position
    Vec3d oldPos = new Vec3d(this.posX, this.posY, this.posZ);
    Vec3d newPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
    RayTraceResult raytraceResult = this.getEntityWorld().rayTraceBlocks(oldPos, newPos, false, true, false);

    // raytrace messes with the positions. get new ones! (not anymore since vec3d is all final now?)
    //oldPos = Vec3d.createVectorHelper(this.posX, this.posY, this.posZ);

    // if we hit something, the collision point is our new position
    if(raytraceResult != null) {
      newPos = new Vec3d(raytraceResult.hitVec.x, raytraceResult.hitVec.y, raytraceResult.hitVec.z);
    }
    //else
    //newPos = Vec3d.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

    Entity entity = this.findEntityOnPath(oldPos, newPos);

    // if we hit something, new collision point!
    if(entity != null) {
      raytraceResult = new RayTraceResult(entity);
    }

    // did we hit a player?
    if(raytraceResult != null && raytraceResult.entityHit != null && raytraceResult.entityHit instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) raytraceResult.entityHit;

      // can we attack said player?
      if(entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
        raytraceResult = null;
      }

      // this check should probably done inside of the loop for accuracy..
    }

    // time to hit the object
    if(raytraceResult != null && !MinecraftForge.EVENT_BUS.post(getProjectileImpactEvent(raytraceResult))) {
      if(raytraceResult.entityHit != null) {
        onHitEntity(raytraceResult);
      }
      else {
        onHitBlock(raytraceResult);
      }
    }

    // crithit particles
    if(this.getIsCritical()) {
      drawCritParticles();
    }

    // MOVEMENT! yay.
    doMoveUpdate();
    // Slowdown
    double slowdown = 1.0d - getSlowdown();

    // bubblez
    if(this.isInWater()) {
      for(int l = 0; l < 4; ++l) {
        float f3 = 0.25F;
        this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
      }

      // more slowdown in water
      slowdown *= 0.60d;
    }

    // phshshshshshs
    if(this.isWet()) {
      this.extinguish();
    }

    // minimalistic slowdown!
    this.motionX *= slowdown;
    this.motionY *= slowdown;
    this.motionZ *= slowdown;
    // gravity
    if(!this.hasNoGravity()) {
      this.motionY -= getGravity();
    }
    for(IProjectileTrait trait : tinkerProjectile.getProjectileTraits()) {
      trait.onMovement(this, getEntityWorld(), slowdown);
    }
    this.setPosition(this.posX, this.posY, this.posZ);

    // tell blocks we collided with, that we collided with them!
    this.doBlockCollisions();
  }

  protected TinkerProjectileImpactEvent getProjectileImpactEvent(RayTraceResult rayTraceResult) {
    return new TinkerProjectileImpactEvent(this, rayTraceResult, tinkerProjectile.getItemStack());
  }

  @Nullable
  @Override
  protected Entity findEntityOnPath(@Nonnull Vec3d start, @Nonnull Vec3d end) {
    if(isDefused()) {
      return null;
    }
    return super.findEntityOnPath(start, end);
  }

  public void drawCritParticles() {
    for(int k = 0; k < 4; ++k) {
      this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * k / 4.0D, this.posY + this.motionY * k / 4.0D, this.posZ + this.motionZ * k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
    }
  }

  protected void doMoveUpdate() {
    this.posX += this.motionX;
    this.posY += this.motionY;
    this.posZ += this.motionZ;
    double f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
    this.rotationPitch = (float) (Math.atan2(this.motionY, f2) * 180.0D / Math.PI);

    // normalize rotations
    while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
      this.prevRotationPitch -= 360.0F;
    }

    while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
      this.prevRotationPitch += 360.0F;
    }

    while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
      this.prevRotationYaw -= 360.0F;
    }

    while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
      this.prevRotationYaw += 360.0F;
    }

    this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
    this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
  }

  /**
   * Factor for the slowdown. 0 = no slowdown, >0 = (1-slowdown)*speed slowdown, <0 = speedup
   */
  public double getSlowdown() {
    return 0.01;
  }

  /**
   * Added to the y-velocity as gravitational pull. Otherwise stuff would simply float midair.
   */
  public double getGravity() {
    return 0.05;
  }

  /**
   * Called by a player entity when they collide with an entity
   */
  @Override
  public void onCollideWithPlayer(@Nonnull EntityPlayer player) {
    if(!this.getEntityWorld().isRemote && this.inGround && this.arrowShake <= 0) {
      boolean pickedUp = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;

      if(pickedUp) {
        pickedUp = tinkerProjectile.pickup(player, pickupStatus != PickupStatus.ALLOWED);
      }

      if(pickedUp) {
        this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        player.onItemPickup(this, 1);
        this.setDead();
      }
    }
  }

  /** NBT stuff **/

  @Override
  public void writeEntityToNBT(NBTTagCompound tags) {
    super.writeEntityToNBT(tags);

    tags.setTag("item", tinkerProjectile.serializeNBT());
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound tags) {
    super.readEntityFromNBT(tags);
    tinkerProjectile.deserializeNBT(tags.getCompoundTag("item"));
  }

  @Override
  public void writeSpawnData(ByteBuf data) {
    data.writeFloat(rotationYaw);

    // shooting entity
    int id = shootingEntity == null ? this.getEntityId() : shootingEntity.getEntityId();
    data.writeInt(id);

    // motion stuff. This has to be sent separately since MC seems to do hardcoded stuff to arrows with this
    data.writeDouble(this.motionX);
    data.writeDouble(this.motionY);
    data.writeDouble(this.motionZ);

    ByteBufUtils.writeItemStack(data, tinkerProjectile.getItemStack());
    ByteBufUtils.writeItemStack(data, tinkerProjectile.getLaunchingStack());
    data.writeFloat(tinkerProjectile.getPower());
  }

  @Override
  public void readSpawnData(ByteBuf data) {
    rotationYaw = data.readFloat();
    shootingEntity = getEntityWorld().getEntityByID(data.readInt());

    this.motionX = data.readDouble();
    this.motionY = data.readDouble();
    this.motionZ = data.readDouble();

    tinkerProjectile.setItemStack(ByteBufUtils.readItemStack(data));
    tinkerProjectile.setLaunchingStack(ByteBufUtils.readItemStack(data));
    tinkerProjectile.setPower(data.readFloat());

    this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
    this.posY -= 0.10000000149011612D;
    this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
  }
}
