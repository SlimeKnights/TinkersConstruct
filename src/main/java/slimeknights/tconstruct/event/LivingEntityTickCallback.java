package slimeknights.tconstruct.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public interface LivingEntityTickCallback {
  Event<LivingEntityTickCallback> EVENT = EventFactory.createArrayBacked(LivingEntityTickCallback.class,
    listeners -> (entity) -> {
      for (LivingEntityTickCallback event : listeners) {
        event.onEntityTick(entity);
      }
    }
  );

  void onEntityTick(LivingEntity entity);
}
