package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithKnockback;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;

/** Modifiable shuriken entity */
public class ThrownShuriken extends Projectile implements ToolProjectile, ProjectileWithPower, ProjectileWithKnockback {

  /**
   * Key to sync the stack to the client
   */
  protected static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ThrownShuriken.class, EntityDataSerializers.ITEM_STACK);
  /**
   * Movement speed in water
   */
  protected static final EntityDataAccessor<Float> WATER_INERTIA = SynchedEntityData.defineId(ThrownShuriken.class, EntityDataSerializers.FLOAT);

  private ItemStack stack = ItemStack.EMPTY;
  private IToolStackView tool = null;
  @Getter
  private float power = 4;
  private float knockback = 0;

  public ThrownShuriken(EntityType<? extends ThrownShuriken> type, Level level) {
    super(type, level);
  }

  public ThrownShuriken(Level level, double pX, double pY, double pZ) {
    this(TinkerTools.thrownShuriken.get(), level);
    this.setPos(pX, pY, pZ);
  }

  public ThrownShuriken(Level level, LivingEntity shooter) {
    this(level, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    this.setOwner(shooter);
  }


  /* Stack */

  /**
   * Updates the stack on the arrow
   */
  private void setStack(ItemStack stack) {
    this.stack = stack;
    this.entityData.set(STACK, stack);
  }

  /**
   * Gets the tool instance, ensuring its created
   */
  private IToolStackView getTool() {
    if (tool == null) {
      tool = ToolStack.from(stack);
    }
    return tool;
  }

  /**
   * Called when the arrow is created to set initial properties.
   * @see ModifiableArrow#onCreate(ItemStack, LivingEntity)
   */
  public void onCreate(ItemStack stack, @Nullable LivingEntity shooter) {
    if (stack.isEmpty()) {
      setStack(ItemStack.EMPTY);
      return;
    }
    setStack(stack);
    // initialize arrow stats
    IToolStackView tool = getTool();
    EntityModifierCapability.getCapability(this).addModifiers(tool.getModifiers());
    this.power = ConditionalStatModifierHook.getModifiedStat(tool, shooter, ToolStats.PROJECTILE_DAMAGE);
    this.entityData.set(WATER_INERTIA, ConditionalStatModifierHook.getModifiedStat(tool, shooter, ToolStats.WATER_INERTIA));
  }

  /** @see ModifiableArrow#shoot(double, double, double, float, float)  */
  @Override
  public void shoot(double pX, double pY, double pZ, float velocity, float inaccuracy) {
    if (!stack.isEmpty()) {
      IToolStackView tool = getTool();
      LivingEntity shooter = ModifierUtil.asLiving(getOwner());
      // apply accuracy, no need to compute this earlier nor store it
      inaccuracy *= ModifierUtil.getInaccuracy(tool, shooter);

      // shoot with new information
      super.shoot(pX, pY, pZ, velocity, inaccuracy);

      // run modifier hooks from the arrow's perspective
      ModDataNBT arrowData = PersistentDataCapability.getOrWarn(this);
      for (ModifierEntry entry : tool.getModifiers()) {
        entry.getHook(ModifierHooks.PROJECTILE_SHOT).onProjectileShoot(tool, entry, shooter, stack, this, null, arrowData, true);
      }
    } else {
      super.shoot(pX, pY, pZ, velocity, inaccuracy);
    }
  }


  /* Physics */

  /** Based on {@link net.minecraft.world.entity.projectile.ThrowableProjectile}, copied instead of extends to change water behavior. */
  @Override
  public void tick() {
    super.tick();

    HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
    boolean teleported = false;
    if (hit.getType() == HitResult.Type.BLOCK) {
      BlockPos pos = ((BlockHitResult)hit).getBlockPos();
      BlockState state = this.level().getBlockState(pos);
      if (state.is(Blocks.NETHER_PORTAL)) {
        this.handleInsidePortal(pos);
        teleported = true;
      } else if (state.is(Blocks.END_GATEWAY)) {
        if (this.level().getBlockEntity(pos) instanceof TheEndGatewayBlockEntity gateway && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
          TheEndGatewayBlockEntity.teleportEntity(this.level(), pos, state, this, gateway);
        }
        teleported = true;
      }
    }

    if (hit.getType() != HitResult.Type.MISS && !teleported && !ForgeEventFactory.onProjectileImpact(this, hit)) {
      this.onHit(hit);
    }

    // update position
    this.checkInsideBlocks();
    Vec3 movement = this.getDeltaMovement();
    double x = this.getX() + movement.x;
    double y = this.getY() + movement.y;
    double z = this.getZ() + movement.z;
    this.updateRotation();

    // apply water
    float speedReduction;
    if (this.isInWater()) {
      for (int i = 0; i < 4; ++i) {
        this.level().addParticle(ParticleTypes.BUBBLE, x - movement.x * 0.25, y - movement.y * 0.25, z - movement.z * 0.25, movement.x, movement.y, movement.z);
      }
      speedReduction = this.entityData.get(WATER_INERTIA);
    } else {
      speedReduction = 0.99F;
    }
    this.setDeltaMovement(movement.scale(speedReduction));

    // apply gravity
    if (!this.isNoGravity()) {
      Vec3 vec31 = this.getDeltaMovement();
      this.setDeltaMovement(vec31.x, vec31.y - 0.03f, vec31.z);
    }

    this.setPos(x, y, z);
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    Entity target = result.getEntity();
    target.hurt(damageSources().thrown(this, this.getOwner()), power);

    Level level = level();
    if (!level.isClientSide) {
      if (knockback > 0 && target instanceof LivingEntity living) {
        // knockback logic based on arrows
        double resistance = Math.max(0, 1 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 motion = this.getDeltaMovement().multiply(1, 0, 1).normalize().scale(knockback * 0.6 * resistance);
        if (motion.lengthSqr() > 0) {
          target.push(motion.x, 0.1f, motion.z);
        }
      }
      level.broadcastEntityEvent(this, (byte) 3); // TODO: find the proper constant for this event ID
      this.discard();
    }
  }

  @Override
  protected void onHitBlock(BlockHitResult result) {
    super.onHitBlock(result);

    // TODO: can we stick in the block like an arrow instead?
    if (!this.isRemoved()) {
      this.spawnAtLocation(stack.copy());
      this.discard();
    }
  }


  /* Stats */

  @Override
  public void setPower(float power) {
    this.power = power;
  }

  @Override
  public void addKnockback(float amount) {
    this.knockback += amount;
  }


  /* Client */

  @Override
  protected void defineSynchedData() {
    this.entityData.define(STACK, ItemStack.EMPTY);
    this.entityData.define(WATER_INERTIA, 0.8f);
  }

  @Override
  public ItemStack getDisplayTool() {
    return this.entityData.get(STACK);
  }

  @Override
  public Component getDisplayName() {
    return getDisplayTool().getDisplayName();
  }

  @Override
  public boolean shouldRenderAtSqrDistance(double pDistance) {
    double d0 = this.getBoundingBox().getSize() * 4.0D;
    if (Double.isNaN(d0)) {
      d0 = 4.0D;
    }

    d0 *= 64.0D;
    return pDistance < d0 * d0;
  }


  /* NBT */
  private static final String KEY_STACK = "stack";
  private static final String KEY_WATER_INERTIA = "water_inertia";

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.put(KEY_STACK, this.stack.save(new CompoundTag()));
    tag.putFloat(KEY_WATER_INERTIA, this.entityData.get(WATER_INERTIA));
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    if (tag.contains(KEY_STACK, CompoundTag.TAG_COMPOUND)) {
      setStack(ItemStack.of(tag.getCompound(KEY_STACK)));
    }
    this.entityData.set(WATER_INERTIA, tag.getFloat(KEY_WATER_INERTIA));
  }
}
