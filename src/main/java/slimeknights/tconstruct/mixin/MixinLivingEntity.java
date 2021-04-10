package slimeknights.tconstruct.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.event.LivingEntityDropXpCallback;
import slimeknights.tconstruct.shared.BlockEvents;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
  @Shadow
  protected int playerHitTimer;
  @Shadow
  protected PlayerEntity attackingPlayer;

  private MixinLivingEntity(EntityType<?> type, World world) {
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
}
