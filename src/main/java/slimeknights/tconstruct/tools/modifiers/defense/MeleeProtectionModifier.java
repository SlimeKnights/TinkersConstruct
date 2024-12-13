package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;

/** @deprecated use {@link ProtectionModule} and {@link slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule} */
@Deprecated(forRemoval = true)
public class MeleeProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final ComputableDataKey<ModifierMaxLevel> KEY = TConstruct.createKey("melee_protection", ModifierMaxLevel::new);

  public MeleeProtectionModifier() {
    super(KEY);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.MELEE).eachLevel(2.5f));
  }

  @Override
  protected void set(ModifierMaxLevel data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    float oldMax = data.getMax();
    super.set(data, slot, scaledLevel, context);
    float newMax = data.getMax();
    if (oldMax != newMax) {
      context.getTinkerData().ifPresent(d -> d.add(TinkerDataKeys.USE_ITEM_SPEED, (newMax - oldMax) * 0.05f));
    }
  }
}
