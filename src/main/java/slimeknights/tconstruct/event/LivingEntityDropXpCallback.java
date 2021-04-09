package slimeknights.tconstruct.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public interface LivingEntityDropXpCallback {
  Event<LivingEntityDropXpCallback> EVENT = EventFactory.createArrayBacked(LivingEntityDropXpCallback.class,
    listeners -> (entity, source, expToDrop) -> {
      TypedActionResult<Integer> result = TypedActionResult.pass(expToDrop);
      for (LivingEntityDropXpCallback event : listeners) {
        result = event.onDropXp(entity, source, expToDrop);

        if (result.getResult() == ActionResult.FAIL) {
          return result;
        }
      }
      return result;
    }
  );

  TypedActionResult<Integer> onDropXp(LivingEntity entity, DamageSource source, int expToDrop);
}
