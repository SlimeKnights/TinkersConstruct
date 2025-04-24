package slimeknights.tconstruct.library.modifiers.fluid.entity;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

/** Fluid effect causing a ranged block interaction */
@Getter
public enum EntityInteractFluidEffect implements FluidEffect<FluidEffectContext.Entity> {
  INSTANCE;

  private final SingletonLoader<EntityInteractFluidEffect> loader = new SingletonLoader<>(this);

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Entity context, FluidAction action) {
    Player player = context.getPlayer();
    if (player != null) {
      Level world = context.getLevel();
      Entity target = context.getTarget();
      // skip if entity is outside world border
      if (!world.getWorldBorder().isWithinBounds(target.blockPosition())) {
        return 0;
      }
      // we have no way of checking if clicking the entity does anything without actually clicking, so just always charge the full amount
      if (action.simulate()) {
        return 1;
      }
      // interact with both hands
      FeatureFlagSet enabled = world.enabledFeatures();
      for (InteractionHand hand : InteractionHand.values()) {
        // if the item is not enabled, give up entirely. Not sure why, its just what vanilla does
        if (!player.getItemInHand(hand).isItemEnabled(enabled)) {
          return 0;
        }
        // translate hit to be relative to entity's position
        // unfortunately, our projectiles always are considered hitting the entity's position making this 0
        Vec3 hit = context.getLocation().subtract(target.position());

        // check if forge wants to override
        InteractionResult result = ForgeHooks.onInteractEntityAt(player, target, hit, hand);
        // skipped: never spectator mode if we made it this far
        if (result == null) {
          // no forge override, so find first success from vanilla hooks
          result = target.interactAt(player, hit, hand);
          if (!result.consumesAction()) {
            result = player.interactOn(context.getTarget(), hand);
          }
        }
        // long range arm swinging
        if (result != InteractionResult.PASS) {
          if (result.consumesAction()) {
            player.swing(hand, true);
            return 1;
          }
          return 0; // failure exits the loop
        }
      }
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return Component.translatable(FluidEffect.getTranslationKey(getLoader()) + ".entity");
  }
}
