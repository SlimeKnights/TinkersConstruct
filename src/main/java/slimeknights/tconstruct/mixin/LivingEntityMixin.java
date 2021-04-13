package slimeknights.tconstruct.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.event.LivingEntityDropXpCallback;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.BlockEvents;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
  @Shadow
  protected int playerHitTimer;
  @Shadow
  protected PlayerEntity attackingPlayer;

  private LivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Shadow
  protected abstract boolean shouldAlwaysDropXp();

  @Shadow
  protected abstract boolean canDropLootAndXp();

  @Shadow
  protected abstract int getCurrentExperience(PlayerEntity player);


  @Inject(method = "jump",at = @At("TAIL"))
  public void jump(CallbackInfo ci) {
    BlockEvents.onLivingJump((LivingEntity)(Object)this);
  }

  @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropXp()V"), cancellable = true)
  public void dropXp(DamageSource source, CallbackInfo ci) {
    ci.cancel();

    if (world.isClient || (!shouldAlwaysDropXp() && (playerHitTimer <= 0 || !canDropLootAndXp() || !world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)))) {
      return;
    }

    int expToDrop = getCurrentExperience(attackingPlayer);
    LivingEntity thisEntity = (LivingEntity) (Object) this;

    expToDrop = LivingEntityDropXpCallback.EVENT.invoker().onDropXp(thisEntity, source, expToDrop).getValue();

    while(expToDrop > 0) {
      int j = ExperienceOrbEntity.roundToOrbSize(expToDrop);
      expToDrop -= j;
      this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
    }
  }

  @Inject(method = "computeFallDamage", at = @At("HEAD"), cancellable = true)
  private void fallDamageEvent(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
    LivingEntity entity = (LivingEntity) (Object) this;

    ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
    if (!(feet.getItem() instanceof SlimeBootsItem)) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    boolean isClient = entity.getEntityWorld().isClient;
    if (!entity.isInSneakingPose() && fallDistance > 2) {
      entity.fallDistance =  0.0F;
      cir.setReturnValue(0);

      if (isClient) {
        entity.setVelocity(entity.getVelocity().x, entity.getVelocity().y * -0.9, entity.getVelocity().z);
        entity.velocityDirty = true;
        entity.setOnGround(false);
        double f = 0.91d + 0.04d;
        // only slow down half as much when bouncing
        entity.setVelocity(entity.getVelocity().x / f, entity.getVelocity().y, entity.getVelocity().z / f);
        TinkerNetwork.getInstance().sendToServer(new BouncedPacket());
      } else {
        cir.setReturnValue(0); // we don't care about previous cancels, since we just bounceeeee
      }

      entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(entity, entity.getVelocity().y);
    } else if (!isClient && entity.isInSneakingPose()) {
      damageMultiplier = 0.2f;
    }
  }
}
