package slimeknights.tconstruct.library.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.library.tools.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.tools.IProjectileStats;
import slimeknights.tconstruct.library.tools.ITinkerProjectile;
import slimeknights.tconstruct.library.tools.TinkerProjectileHandler;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

// have to base this on EntityArrow, otherwise minecraft does derp things because everything is handled based on class.
public abstract class EntityProjectileBase extends EntityArrow implements IEntityAdditionalSpawnData {
  //public final static String woodSound = Reference.resource("woodHit");
  //public final static String stoneSound = Reference.resource("stoneHit");

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

  public EntityProjectileBase(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
    this(world);

    this.shootingEntity = player;

    canBePickedUp = player.isCreative() ? PickupStatus.CREATIVE_ONLY : PickupStatus.ALLOWED;

    // stuff from the arrow
    this.setLocationAndAngles(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
    this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
    this.posY -= 0.10000000149011612D;
    this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
    this.setPosition(this.posX, this.posY, this.posZ);
    //this.yOffset = 0.0F;
    this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
    this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
    this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
    this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, accuracy);

    // our stuff
    tinkerProjectile.setItemStack(stack);
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

  @Override
  protected ItemStack getArrowStack() {
    return tinkerProjectile.getItemStack();
  }

  protected void playHitBlockSound(float speed) {
    // 1.9
    /*
    Block block = worldObj.getBlock(x, y, z);
    if(block != null && block.blockMaterial == Material.wood)
      worldObj.playSoundAtEntity(this, woodSound, 1.0f, 1.0f);
    else {
      worldObj.playSoundAtEntity(this, stoneSound, 1.0f, 1.0f);
    }

    if(block != null)
      worldObj.playSoundAtEntity(this, block.stepSound.getBreakSound(), 0.7f, 1.0f);*/
  }

  protected void playHitEntitySound() {

  }

  /**
   * How deep the item enters stuff it hits. Should be bigger for bigger objects, and smaller for smaller objects.
   * 1.0f is exactly halfway in.
   */
  protected double getStuckDepth() {
    return 0.5f;
  }

  protected void doLivingHit(EntityLivingBase entityHit) {
    /*
    float knockback = returnStack.getTagCompound().getCompoundTag("InfiTool").getFloat("Knockback");
    if(shootingEntity instanceof EntityLivingBase)
      knockback += EnchantmentHelper.getKnockbackModifier((EntityLivingBase) shootingEntity, entityHit);

    if (!this.worldObj.isRemote)
    {
      entityHit.setArrowCountInEntity(entityHit.getArrowCountInEntity() + 1);
    }

    if (knockback > 0)
    {
      double horizontalSpeed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

      if (horizontalSpeed > 0.0F)
      {
        entityHit.addVelocity(this.motionX * (double) knockback * 0.6000000238418579D / horizontalSpeed, 0.1D, this.motionZ * (double) knockback * 0.6000000238418579D / (double) horizontalSpeed);
      }
    }

    if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase)
    {
      EnchantmentHelper.func_151384_a(entityHit, this.shootingEntity);
      EnchantmentHelper.func_151385_b((EntityLivingBase)this.shootingEntity, entityHit);
    }

    if (this.shootingEntity != null && entityHit != this.shootingEntity && entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
    {
      ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new SPacketChangeGameState(6, 0.0F));
    }
    */
  }

  public void onHitBlock(RayTraceResult raytraceResult) {
    BlockPos blockpos = raytraceResult.getBlockPos();
    this.xTile = blockpos.getX();
    this.yTile = blockpos.getY();
    this.zTile = blockpos.getZ();
    IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
    this.inTile = iblockstate.getBlock();
    this.inData = this.inTile.getMetaFromState(iblockstate);
    this.motionX = (double) ((float) (raytraceResult.hitVec.xCoord - this.posX));
    this.motionY = (double) ((float) (raytraceResult.hitVec.yCoord - this.posY));
    this.motionZ = (double) ((float) (raytraceResult.hitVec.zCoord - this.posZ));
    float speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
    this.posX -= this.motionX / (double) speed * 0.05000000074505806D;
    this.posY -= this.motionY / (double) speed * 0.05000000074505806D;
    this.posZ -= this.motionZ / (double) speed * 0.05000000074505806D;

    playHitBlockSound(speed);

    this.inGround = true;
    this.arrowShake = 7;
    this.setIsCritical(false);

    if(iblockstate.getMaterial() != Material.AIR) {
      this.inTile.onEntityCollidedWithBlock(this.worldObj, blockpos, iblockstate, this);
    }

    this.defused = true; // defuse it so it doesn't hit stuff anymore, being weird
  }

  public void onHitEntity(RayTraceResult raytraceResult) {
    ItemStack item = tinkerProjectile.getItemStack();
    boolean bounceOff = false;
    // deal damage if we have everything
    if(item != null && item.getItem() instanceof ToolCore && raytraceResult.entityHit instanceof EntityLivingBase && this.shootingEntity instanceof EntityLivingBase) {
      EntityLivingBase attacker = (EntityLivingBase) this.shootingEntity;
      EntityLivingBase target = (EntityLivingBase) raytraceResult.entityHit;

      // find the actual itemstack in the players inventory
      ItemStack inventoryItem = tinkerProjectile.getMatchingItemstackFromInventory(attacker, false);
      if(inventoryItem == null || inventoryItem.getItem() != item.getItem()) {
        // backup, use saved itemstack
        inventoryItem = item;
      }

      // remove stats from held items
      unequip(attacker, EntityEquipmentSlot.OFFHAND);
      unequip(attacker, EntityEquipmentSlot.MAINHAND);

      // apply stats from projectile
      if(item.getItem() instanceof IProjectileStats) {
        attacker.getAttributeMap().applyAttributeModifiers(((IProjectileStats) item.getItem()).getProjectileAttributeModifier(inventoryItem));
      }

      // deal the damage
      float speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      bounceOff = dealDamage(speed, inventoryItem, attacker, target);

      // remove stats from projectile
      // apply stats from projectile
      if(item.getItem() instanceof IProjectileStats) {
        attacker.getAttributeMap().removeAttributeModifiers(((IProjectileStats) item.getItem()).getProjectileAttributeModifier(inventoryItem));
      }

      // readd stats from held items
      equip(attacker, EntityEquipmentSlot.MAINHAND);
      equip(attacker, EntityEquipmentSlot.OFFHAND);

      if(!bounceOff) {
        doLivingHit(target);
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
    if(stack != null) {
      entity.getAttributeMap().removeAttributeModifiers(stack.getAttributeModifiers(slot));
    }
  }

  private void equip(EntityLivingBase entity, EntityEquipmentSlot slot) {
    ItemStack stack = entity.getItemStackFromSlot(slot);
    if(stack != null) {
      entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
    }
  }

  // returns true if it was successful
  public boolean dealDamage(float speed, ItemStack item, EntityLivingBase attacker, EntityLivingBase target) {
    return ToolHelper.attackEntity(item, (ToolCore) item.getItem(), attacker, target, true);
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

    // boioioiooioing
    if(this.arrowShake > 0) {
      --this.arrowShake;
    }

    // If we don't have our rotation set correctly, infer it from our motion direction
    if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D / Math.PI);
    }

    // we previously hit something. Check if the block is still there.
    BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
    IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

    if(iblockstate.getMaterial() != Material.AIR) {
      AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.worldObj, blockpos);

      if(axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).isVecInside(new Vec3d(this.posX, this.posY, this.posZ))) {
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
  protected void updateInGround(IBlockState state) {
    Block block = state.getBlock();
    int meta = block.getMetaFromState(state);

    // check if it's still the same block
    if(block == this.inTile && meta == this.inData) {
      ++this.ticksInGround;

      if(this.ticksInGround >= 1200) {
        this.setDead();
      }
    }
    else {
      this.inGround = false;
      this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
      this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
      this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
      this.ticksInGround = 0;
      this.ticksInAir = 0;
    }

    ++this.timeInGround;
  }

  // update while traveling
  protected void updateInAir() {
    // tick tock
    this.timeInGround = 0;
    ++this.ticksInAir;

    // do a raytrace from old to new position
    Vec3d oldPos = new Vec3d(this.posX, this.posY, this.posZ);
    Vec3d newPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
    RayTraceResult raytraceResult = this.worldObj.rayTraceBlocks(oldPos, newPos, false, true, false);

    // raytrace messes with the positions. get new ones! (not anymore since vec3d is all final now?)
    //oldPos = Vec3d.createVectorHelper(this.posX, this.posY, this.posZ);

    // if we hit something, the collision point is our new position
    if(raytraceResult != null) {
      newPos = new Vec3d(raytraceResult.hitVec.xCoord, raytraceResult.hitVec.yCoord, raytraceResult.hitVec.zCoord);
    }
    //else
    //newPos = Vec3d.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

    Entity entity = this.func_184551_a(oldPos, newPos);

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
    if(raytraceResult != null) {
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
        this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX, this.motionY, this.motionZ);
      }

      // more slowdown in water
      slowdown = 1d - 20d * getSlowdown();
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
    this.motionY -= getGravity();
    this.setPosition(this.posX, this.posY, this.posZ);

    // tell blocks we collided with, that we collided with them!
    this.doBlockCollisions();
  }

  @Override
  protected Entity func_184551_a(Vec3d p_184551_1_, Vec3d p_184551_2_) {
    if(isDefused()) {
      return null;
    }
    return super.func_184551_a(p_184551_1_, p_184551_2_);
  }

  public void drawCritParticles() {
    for(int k = 0; k < 4; ++k) {
      this.worldObj.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double) k / 4.0D, this.posY + this.motionY * (double) k / 4.0D, this.posZ + this.motionZ * (double) k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
    }
  }

  protected void doMoveUpdate() {
    this.posX += this.motionX;
    this.posY += this.motionY;
    this.posZ += this.motionZ;
    double f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
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
  protected double getSlowdown() {
    return 0.01;
  }

  /**
   * Added to the y-velocity as gravitational pull. Otherwise stuff would simply float midair.
   */
  protected double getGravity() {
    return 0.05;
  }

  /**
   * Called by a player entity when they collide with an entity
   */
  @Override
  public void onCollideWithPlayer(EntityPlayer player) {
    if(!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
      boolean pickedUp = this.canBePickedUp == EntityArrow.PickupStatus.ALLOWED || this.canBePickedUp == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;

      if(pickedUp) {
        pickedUp = tinkerProjectile.pickup(player, canBePickedUp != PickupStatus.ALLOWED);
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
  }

  @Override
  public void readSpawnData(ByteBuf data) {
    rotationYaw = data.readFloat();
    shootingEntity = worldObj.getEntityByID(data.readInt());

    this.motionX = data.readDouble();
    this.motionY = data.readDouble();
    this.motionZ = data.readDouble();

    tinkerProjectile.setItemStack(ByteBufUtils.readItemStack(data));
  }
}

