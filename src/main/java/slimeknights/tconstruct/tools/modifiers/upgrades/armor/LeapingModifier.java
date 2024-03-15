package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.unserializable.ArmorStatModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

public class LeapingModifier extends IncrementalModifier {
  private static final TinkerDataKey<Float> LEAPING = TConstruct.createKey("leaping");
  public LeapingModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(LeapingModifier::onLivingFall);
    MinecraftForge.EVENT_BUS.addListener(LeapingModifier::onLivingJump);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new ArmorStatModule(LEAPING, 1, false));
  }

  /** Reduce fall distance for fall damage */
  private static void onLivingFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntity();
    float boost = ArmorStatModule.getStat(entity, LEAPING);
    if (boost > 0) {
      event.setDistance(Math.max(event.getDistance() - boost, 0));
    }
  }

  /** Called on jumping to boost the jump height of the entity */
  private static void onLivingJump(LivingJumpEvent event) {
    LivingEntity entity = event.getEntity();
    float boost = ArmorStatModule.getStat(entity, LEAPING);
    if (boost > 0) {
      entity.setDeltaMovement(entity.getDeltaMovement().add(0, boost * 0.1, 0));
    }
  }
}
