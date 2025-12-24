package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithKnockback;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Objects;

/** Fishing hook that deals damage and can be used as a grappling hook */
public class CombatFishingHook extends FishingHook implements ProjectileWithKnockback, ProjectileWithPower {
  private static final float PI = (float) Math.PI;
  /** Force to apply for grapple. Will be divided by the square root of the desired distance. */
  private static final float GRAPPLE_STRENGTH = 0.58f;
  private static final EntityDataAccessor<Byte> GRAPPLE = SynchedEntityData.defineId(CombatFishingHook.class, EntityDataSerializers.BYTE);
  private static final EntityDataAccessor<Boolean> COLLECTING = SynchedEntityData.defineId(CombatFishingHook.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<MaterialVariantId> MATERIAL = SynchedEntityData.defineId(CombatFishingHook.class, MaterialVariantId.DATA_ACCESSOR);

  /** Damage dealt by the fishing hook */
  @Getter @Setter
  private float power = 0;
  /** Extra power for pulling entities towards ourself */
  private float knockback = 0;
  /** Velocity at the time the projectile hit the entity, used for damage calculations */
  private double impactVelocity = 1;
  /** Last block state hit by the bobber, used for grapling to freeze the projectile in the block */
  private BlockState wallState = null;

  public CombatFishingHook(EntityType<? extends FishingHook> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  // set velocity to 0.6 for vanilla behavior
  public CombatFishingHook(Player player, Level level, int luck, int lure, float velocity, float inaccuracy) {
    super(TinkerTools.fishingHook.get(), level, luck, lure);
    this.setOwner(player);
    float xRot = player.getXRot();
    float yRot = player.getYRot();
    float yAngle = (-yRot * PI / 180f) - PI;
    float dz = Mth.cos(yAngle);
    float dx = Mth.sin(yAngle);
    // position
    this.moveTo(
      player.getX() - dx * 0.3,
      player.getEyeY(),
      player.getZ() - dz * 0.3,
      yRot, xRot);
    // speed
    float xAngle = -xRot * (PI / 180F);
    float yCos = -Mth.cos(xAngle);
    float ySin = Mth.sin(xAngle);
    Vec3 deltaMovement = new Vec3(-dx, Mth.clamp(-ySin / yCos, -5f, 5f), -dz);
    double length = deltaMovement.length();
    double maxRandom = 0.03 * inaccuracy * inaccuracy;
    deltaMovement = deltaMovement.multiply(
      velocity / length + this.random.triangle(0.5, maxRandom),
      velocity / length + this.random.triangle(0.5, maxRandom),
      velocity / length + this.random.triangle(0.5, maxRandom));
    this.setDeltaMovement(deltaMovement);
    this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (180 / PI)));
    this.setXRot((float)(Mth.atan2(deltaMovement.y, deltaMovement.horizontalDistance()) * (180 / PI)));
    this.yRotO = this.getYRot();
    this.xRotO = this.getXRot();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(GRAPPLE, (byte) GrappleType.NONE.ordinal());
    this.entityData.define(COLLECTING, false);
    this.entityData.define(MATERIAL, IMaterial.UNKNOWN_ID);
  }

  /** Gets the currently displayed material */
  public MaterialVariantId getMaterial() {
    return this.entityData.get(MATERIAL);
  }

  /** Gets the currently displayed material */
  public void setMaterial(MaterialVariantId material) {
    this.entityData.set(MATERIAL, material);
  }

  @Override
  public void addKnockback(float amount) {
    this.knockback += amount;
  }

  /** Enables grapple functionality */
  public void setGrapple(GrappleType type) {
    this.entityData.set(GRAPPLE, (byte)type.ordinal());
  }

  /** Enables collecting functionality */
  public void setCollecting() {
    this.entityData.set(COLLECTING, true);
  }

  /** Checks whether grapple is active */
  private boolean isGrapple() {
    return entityData.get(GRAPPLE) != GrappleType.NONE.ordinal();
  }

  /** Checks if drill is active */
  private boolean isDrill() {
    return entityData.get(GRAPPLE) == GrappleType.DRILL.ordinal();
  }

  /** Checks if collecting is active */
  private boolean isCollecting() {
    return entityData.get(COLLECTING);
  }

  @Override
  public float getDamage() {
    double velocity;
    if (getHookedIn() != null) {
      velocity = this.impactVelocity;
    } else {
      velocity = getDeltaMovement().length();
    }
    // round to the nearest 0.1
    // we start by multiplying by 20 to make the damage approximately 2/3 of a bow (as bows multiply velocity by 3)
    return Mth.ceil(Mth.clamp(velocity * this.power * 20, 0, Integer.MAX_VALUE)) / 10f;
  }


  /* Damage and knockback */

  /** Damages the rod if locatable */
  private void damageRod() {
    // we damage on both cast and release to prevent some cheese with some modifiers and swapping items post cast
    if (!level().isClientSide && getOwner() instanceof LivingEntity living) {
      ItemStack stack = living.getMainHandItem();
      InteractionHand hand = InteractionHand.MAIN_HAND;
      // must be able to cast
      if (!stack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
        stack = living.getOffhandItem();
        if (!stack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
          return;
        }
        hand = InteractionHand.OFF_HAND;
      }
      // must be modifiable
      if (stack.is(TinkerTags.Items.MODIFIABLE)) {
        ToolDamageUtil.damageAnimated(ToolStack.from(stack), 1, living, hand);
      }
    }
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    super.onHitEntity(result);
    // store the impact velocity to scale our damage later
    impactVelocity = this.getDeltaMovement().length();
    damageRod();
  }

  @Override
  protected boolean canHitEntity(Entity target) {
    return super.canHitEntity(target) || (target.isAlive() && isCollecting() && (target.getType().is(TinkerTags.EntityTypes.COLLECTABLES) || target instanceof AbstractArrow));
  }

  @Override
  protected void pullEntity(Entity target) {
    Entity owner = this.getOwner();
    if (owner != null) {
      // if requested, collect the targeted item
      // include arrows directly for modded arrow compat
      boolean collectable = target.getType().is(TinkerTags.EntityTypes.COLLECTABLES) || target instanceof ItemEntity || target instanceof AbstractArrow;
      if (collectable && isCollecting()) {
        if (owner instanceof Player player) {
          target.playerTouch(player);
          if (target.isRemoved()) {
            return;
          }
        }
      }

      // don't damage anything thats a collectable, mainly affects items
      if (power > 0 && !collectable) {
        // mark target hurt
        if (owner instanceof LivingEntity living) {
          living.setLastHurtMob(target);
        }
        float damage = getDamage();
        DamageSource source = CombatHelper.damageSource(TinkerDamageTypes.FISHING_HOOK, this, owner);
        LivingEntity targetLiving = ToolAttackUtil.getLivingEntity(target);
        // don't want to apply default knockback, we will apply our own later in the opposite direction
        AttributeInstance knockback = ToolAttackUtil.disableKnockback(targetLiving);
        // actually hurt the entity
        float oldHealth = targetLiving != null ? targetLiving.getHealth() : 0;
        if (target.hurt(source, damage)) {
          if (!this.level().isClientSide && owner instanceof LivingEntity ownerLiving) {
            if (targetLiving != null) {
              EnchantmentHelper.doPostHurtEffects(targetLiving, owner);
            }

            // run modifier hook
            modifierHook: {
              // find out which stack was used
              ItemStack stack = ownerLiving.getMainHandItem();
              if (!stack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
                stack = ownerLiving.getOffhandItem();
                if (!stack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
                  break modifierHook;
                }
              }
              // if it's a modifiable item, run the hook
              if (stack.is(TinkerTags.Items.MODIFIABLE)) {
                // calculate how much damage we actually did
                float damageDealt = damage;
                if (targetLiving != null) {
                  damageDealt = oldHealth - targetLiving.getHealth();
                }
                // actually run the hook
                IToolStackView tool = ToolStack.from(stack);
                for (ModifierEntry modifier : tool.getModifiers()) {
                  modifier.getHook(ModifierHooks.LAUNCHER_HIT).onLauncherHitEntity(tool, modifier, this, ownerLiving, target, targetLiving, damageDealt);
                }
              }
            }
          }
        }
        ToolAttackUtil.enableKnockback(knockback);
      }
      // pull the target, bonus pulling if we have punch
      Vec3 knockback = new Vec3(owner.getX() - this.getX(), owner.getY() - this.getY(), owner.getZ() - this.getZ());
      float scale = 0.1f;
      if (this.knockback > 0) {
        // use the normalized distance for the punch bonus, keep the original 0.1 as scale of total for vanilla consistency
        scale += this.knockback * 0.25f * Mth.invSqrt(knockback.lengthSqr());
      }
      target.setDeltaMovement(target.getDeltaMovement().add(knockback.scale(scale)));

      // if still alive and drill attack, we shoot towards them
      if (isDrill()) {
        pullGrapple(owner);
      }
    }
  }


  /* Grappling */

  /** Pulls in the given entity using grapple force */
  private void pullGrapple(Entity owner) {
    // pull the owner, bonus pulling if we have knockback
    Vec3 knockback = new Vec3(this.getX() - owner.getX(), this.getY() - owner.getY(), this.getZ() - owner.getZ());
    // goal is dividing the scale by the square root of the length, computed as the negative 4th root of the length squared to reduce sqrt calls.
    knockback = knockback.scale(GRAPPLE_STRENGTH * Math.pow(knockback.lengthSqr(), -0.25f));
    owner.push(knockback.x, knockback.y, knockback.z);
    if (isDrill() && owner instanceof Player player) {
      player.startAutoSpinAttack(20);
    }
    if (owner instanceof ServerPlayer player) {
      player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement()));
    }
  }

  @Override
  public int retrieve(ItemStack stack) {
    Entity owner = this.getOwner();
    if (this.onGround() || wallState != null) {
      if (owner != null && isGrapple()) {
        pullGrapple(owner);
      }
      // run modifier hook
      if (owner instanceof LivingEntity living && stack.is(TinkerTags.Items.MODIFIABLE)) {
        IToolStackView tool = ToolStack.from(stack);
        BlockPos pos = blockPosition();
        for (ModifierEntry entry : tool.getModifiers()) {
          entry.getHook(ModifierHooks.LAUNCHER_HIT).onLauncherHitBlock(tool, entry, this, living, pos);
        }
      }
      // do at least 2 damage, but not more than 3, practically this should always be 2
      return Mth.clamp(super.retrieve(stack), 2, 3);
    }
    // deal 3 damage for mob hooking instead of 5
    return Math.min(super.retrieve(stack), 3);
  }

  @Override
  protected void onHitBlock(BlockHitResult result) {
    super.onHitBlock(result);
    // TODO: this isn't super consistent at sticking in the walls
    if (isGrapple()) {
      this.setOnGround(true);
      this.wallState = level().getBlockState(result.getBlockPos());
      Vec3 hit = result.getLocation();
      Vec3 offset = hit.subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(offset);
      this.setPosRaw(hit.x, hit.y, hit.z);
    }
  }

  /** Checks if we should start falling */
  private boolean shouldFall() {
    return wallState != null && level().noCollision((new AABB(position(), position())).inflate(0.06D));
  }

  /** Makes us fall out of the connected block */
  private void startFalling() {
    this.wallState = null;
    Vec3 velocity = this.getDeltaMovement();
    this.setDeltaMovement(velocity.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
    this.life = 0;
  }

  @Override
  public void move(MoverType type, Vec3 pos) {
    // ignore gravity and other motions if in the wall currently
    if (type == MoverType.SELF && wallState != null) {
      if (wallState != level().getBlockState(blockPosition()) && shouldFall()) {
        startFalling();
      } else {
        return;
      }
    }
    // normal movement if not in a wall
    super.move(type, pos);
    // if someone else pushed us, reset movement
    if (type != MoverType.SELF && this.shouldFall()) {
      this.startFalling();
    }
  }

  @Override
  public void tick() {
    // if in the wall, continue ticking life
    int oldLife = this.life;
    super.tick();
    if (this.wallState != null && !level().isClientSide) {
      this.life = oldLife + 1;
      if (this.life >= 1200) {
        this.discard();
      }
    }
  }

  /** Grappling behavior options */
  public enum GrappleType { NONE, DASH, DRILL }


  /* NBT */
  private static final String TAG_MATERIAL = "material";

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putString(TAG_MATERIAL, getMaterial().toString());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    if (tag.contains(TAG_MATERIAL)) {
      setMaterial(Objects.requireNonNullElse(MaterialVariantId.tryParse(tag.getString(TAG_MATERIAL)), IMaterial.UNKNOWN_ID));
    }
  }
}
