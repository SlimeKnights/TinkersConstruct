package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.unserializable.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

public class RicochetModifier extends Modifier {
  private static final TinkerDataKey<Integer> LEVELS = TConstruct.createKey("ricochet");
  public RicochetModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(this::livingKnockback);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new ArmorLevelModule(LEVELS, false));
  }

  /** Called on knockback to adjust player knockback */
  private void livingKnockback(LivingKnockBackEvent event) {
    event.getEntityLiving().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      int levels = data.get(LEVELS, 0);
      if (levels > 0) {
        // adds +20% knockback per level
        event.setStrength(event.getStrength() * (1 + levels * 0.2f));
      }
    });
  }
}
