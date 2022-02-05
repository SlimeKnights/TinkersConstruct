package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

public class MithridatismModifier extends TotalArmorLevelModifier {
  private static final TinkerDataKey<Integer> MITHRIDATISM = TConstruct.createKey("mithridatism");
  public MithridatismModifier() {
    super(MITHRIDATISM, true);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PotionApplicableEvent.class, MithridatismModifier::isPotionApplicable);
  }

  /** Prevents poison on the entity */
  private static void isPotionApplicable(PotionApplicableEvent event) {
    if (event.getPotionEffect().getEffect() == MobEffects.POISON && ModifierUtil.getTotalModifierLevel(event.getEntityLiving(), MITHRIDATISM) > 0) {
      event.setResult(Result.DENY);
    }
  }
}
