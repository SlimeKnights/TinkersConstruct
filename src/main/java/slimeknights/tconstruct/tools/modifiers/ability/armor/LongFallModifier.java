package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** @deprecated use {@link BlockDamageSourceModule} */
@Deprecated
public class LongFallModifier extends NoLevelsModifier {
  @Override
  public boolean isSourceBlocked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return source.isFall();
  }
}
