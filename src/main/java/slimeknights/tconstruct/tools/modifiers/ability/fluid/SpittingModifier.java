package slimeknights.tconstruct.tools.modifiers.ability.fluid;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.fluid.TankModule;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluid;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;

/** Modifier that fires fluid as a projectile */
public class SpittingModifier extends Modifier implements GeneralInteractionModifierHook {
  private TankModule tank;

  @Override
  protected void registerHooks(Builder builder) {
    builder.addHook(this, TinkerHooks.CHARGEABLE_INTERACT);
    tank = new TankModule(FluidAttributes.BUCKET_VOLUME, true);
    builder.addModule(tank);
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      FluidStack fluid = tank.getFluid(tool);
      if (fluid.getAmount() >= (1 + 2 * (modifier.getLevel() - 1)) && SpillingFluidManager.INSTANCE.contains(fluid.getFluid())) {
        ModifierUtil.startUsingItemWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    ScopeModifier.scopingUsingTick(tool, entity, getUseDuration(tool, modifier) - timeLeft);
  }

  @Override
  public boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    ScopeModifier.stopScoping(entity);
    if (!entity.level.isClientSide) {
      int chargeTime = getUseDuration(tool, modifier) - timeLeft;
      if (chargeTime > 0) {
        // find the fluid to spit
        FluidStack fluid = tank.getFluid(tool);
        if (!fluid.isEmpty()) {
          SpillingFluid recipe = SpillingFluidManager.INSTANCE.find(fluid.getFluid());
          if (recipe.hasEffects()) {
            // projectile stats
            float charge = ModifierUtil.getToolCharge(tool, chargeTime);
            // power - size of each individual projectile
            float power = charge * ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.PROJECTILE_DAMAGE);
            // level acts like multishot level, meaning higher produces more projectiles
            int level = modifier.getLevel();
            // amount is the amount per projectile, total cost is amount times level (every other shot is free)
            // if its 0, that means we have only a couple mb left
            int amount = Math.min(fluid.getAmount(), (int)(recipe.getAmount(fluid) * power) * level) / level;
            if (amount > 0) {
              // other stats now that we know we are shooting
              // velocity determines how far it goes, does not impact damage unlike bows
              float velocity = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.VELOCITY) * charge * 3.0f;
              float inaccuracy = ModifierUtil.getInaccuracy(tool, entity, velocity);

              // multishot stuff
              int shots = 1 + 2 * (level - 1);
              float startAngle = ModifiableLauncherItem.getAngleStart(shots);
              int primaryIndex = shots / 2;
              for (int shotIndex = 0; shotIndex < shots; shotIndex++) {
                FluidSpitEntity spit = new FluidSpitEntity(entity.level, entity, new FluidStack(fluid, amount), (int)Math.ceil(power));

                // setup projectile target
                Vector3f targetVector = new Vector3f(entity.getViewVector(1.0f));
                float angle = startAngle + (10 * shotIndex);
                targetVector.transform(new Quaternion(new Vector3f(entity.getUpVector(1.0f)), angle, true));
                spit.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity, inaccuracy);

                // store all modifiers on the spit
                spit.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(tool.getModifiers()));

                // fetch the persistent data for the arrow as modifiers may want to store data
                NamespacedNBT arrowData = PersistentDataCapability.getOrWarn(spit);
                // let modifiers set properties
                for (ModifierEntry entry : tool.getModifierList()) {
                  entry.getHook(TinkerHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, entity, spit, null, arrowData, shotIndex == primaryIndex);
                }

                // finally, fire the projectile
                entity.level.addFreshEntity(spit);
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, SoundSource.PLAYERS, 1.0F, 1.0F / (entity.level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));

              }

              // consume the fluid and durability
              fluid.shrink(amount * level);
              tank.setFluid(tool, fluid);
              ToolDamageUtil.damageAnimated(tool, shots, entity, entity.getUsedItemHand());
            }
          }
        }
      }
    }
    return true;
  }

  /** Projectile entity for spitting */
  public static class FluidSpitEntity extends LlamaSpit {
    private static final EntityDataAccessor<FluidStack> FLUID = SynchedEntityData.defineId(FluidSpitEntity.class, TinkerFluids.FLUID_DATA_SERIALIZER);

    @Setter
    private int power = 1;
    @Setter @Getter
    private int knockback = 1;
    public FluidSpitEntity(EntityType<? extends FluidSpitEntity> type, Level level) {
      super(type, level);
    }

    public FluidSpitEntity(Level level, LivingEntity owner, FluidStack fluid, int power) {
      this(TinkerModifiers.fluidSpitEntity.get(), level);
      this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
      this.setOwner(owner);
      this.setFluid(fluid);
      this.setPower(power);
    }

    /** Gets the fluid for this spit */
    public FluidStack getFluid() {
      return this.entityData.get(FLUID);
    }

    /** Sets the fluid for this spit */
    public void setFluid(FluidStack fluid) {
      this.entityData.set(FLUID, fluid);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
      FluidStack fluid = getFluid();
      if (!level.isClientSide && !fluid.isEmpty() && getOwner() instanceof LivingEntity living) {
        SpillingFluid recipe = SpillingFluidManager.INSTANCE.find(fluid.getFluid());
        Entity target = result.getEntity();
        if (recipe.hasEffects()) {
          recipe.applyEffects(fluid.copy(), power, new ToolAttackContext(
            living, living instanceof Player p ? p : null, InteractionHand.MAIN_HAND,
            target, ToolAttackUtil.getLivingEntity(target), false, 1.0f, false));
        }
        // apply knockback to the entity regardless of fluid type
        if (knockback > 0) {
          Vec3 vec3 = this.getDeltaMovement().multiply(1, 0, 1).normalize().scale(knockback * 0.6);
          if (vec3.lengthSqr() > 0) {
            target.push(vec3.x, 0.1, vec3.z);
          }
        }
      }
    }


    /* Network */

    @Override
    protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(FLUID, FluidStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putInt("power", power);
      nbt.putInt("knockback", knockback);
      FluidStack fluid = getFluid();
      if (!fluid.isEmpty()) {
        nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
      }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      this.power = nbt.getInt("power");
      this.knockback = nbt.getInt("knockback");
      setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
    }
  }
}
