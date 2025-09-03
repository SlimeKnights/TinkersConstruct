package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithKnockback;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

/** Fishing hook that deals damage and can be used as a grappling hook */
public class CombatFishingHook extends FishingHook implements ProjectileWithKnockback, ProjectileWithPower {
  private static final float PI = (float) Math.PI;
  /** Force to apply for grapple. Will be divided by the square root of the desired distance. */
  private static final float GRAPPLE_STRENGTH = 0.58f;
  private static final EntityDataAccessor<Boolean> GRAPPLE = SynchedEntityData.defineId(CombatFishingHook.class, EntityDataSerializers.BOOLEAN);

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
    setGrapple(false);
  }

  // set velocity to 0.6 for vanilla behavior
  public CombatFishingHook(Player player, Level level, int luck, int lure, float velocity, float inaccuracy) {
    super(TinkerTools.fishingHook.get(), level, luck, lure);
    setGrapple(false);
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
    this.entityData.define(GRAPPLE, false);
  }

  @Override
  public void addKnockback(float amount) {
    this.knockback += amount;
  }

  /** Sets the grapple level, causing the hook to pull the player when retrieved */
  public void setGrapple(boolean value) {
    this.entityData.set(GRAPPLE, value);
  }

  /** Gets the current grapple amount */
  public boolean isGrapple() {
    return entityData.get(GRAPPLE);
  }


  /* Damage and knockback */

  @Override
  protected void onHitEntity(EntityHitResult result) {
    super.onHitEntity(result);
    // store the impact velocity to scale our damage later
    impactVelocity = this.getDeltaMovement().length();
  }

  @Override
  protected void pullEntity(Entity target) {
    Entity owner = this.getOwner();
    if (owner != null) {
      // TODO: probably want a modifier that prevents damage
      // TODO: consider a tag for the immune entities instead of just the instance of check
      if (power > 0 && !(target instanceof ItemEntity)) {
        // mark target hurt
        if (owner instanceof LivingEntity living) {
          living.setLastHurtMob(target);
        }
        // setup damage
        float damage = Mth.ceil(Mth.clamp(this.impactVelocity * this.power * 10, 0, Integer.MAX_VALUE)) / 10f;
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
                  modifier.getHook(ModifierHooks.LAUNCHER_HIT).onToolProjectileHit(tool, modifier, this, ownerLiving, target, targetLiving, damageDealt);
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
    }
  }


  /* Grappling */

  @Override
  public int retrieve(ItemStack stack) {
    Entity owner = this.getOwner();
    if (isGrapple() && (this.onGround() || wallState != null) && owner != null) {
      // pull the owner, bonus pulling if we have knockback
      Vec3 knockback = new Vec3(this.getX() - owner.getX(), this.getY() - owner.getY(), this.getZ() - owner.getZ());
      // goal is dividing the scale by the square root of the length, computed as the negative 4th root of the length squared to reduce sqrt calls.
      knockback = knockback.scale(GRAPPLE_STRENGTH * Math.pow(knockback.lengthSqr(), -0.25f));
      owner.push(knockback.x, knockback.y, knockback.z);
      if (owner instanceof ServerPlayer player) {
        player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement()));
      }
      return Math.max(2, super.retrieve(stack));
    }
    return super.retrieve(stack);
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
}
