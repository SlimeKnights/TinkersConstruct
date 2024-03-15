package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.unserializable.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

public class MithridatismModifier extends NoLevelsModifier {
  private static final TinkerDataKey<Integer> MITHRIDATISM = TConstruct.createKey("mithridatism");
  public MithridatismModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, Applicable.class, MithridatismModifier::isPotionApplicable);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new ArmorLevelModule(MITHRIDATISM, false));
  }

  /** Prevents poison on the entity */
  private static void isPotionApplicable(Applicable event) {
    if (event.getEffectInstance().getEffect() == MobEffects.POISON && ArmorLevelModule.getLevel(event.getEntity(), MITHRIDATISM) > 0) {
      event.setResult(Result.DENY);
    }
  }
}
