package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.Comparator;

public class FarsightedModifier extends IncrementalModifier {
  private final ResourceLocation[] SLOT_KEYS = Arrays.stream(EquipmentSlot.values())
                                                     .sorted(Comparator.comparing(EquipmentSlot::getFilterFlag))
                                                     .map(slot -> TConstruct.getResource("farsighted_" + slot.getName()))
                                                     .toArray(ResourceLocation[]::new);

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getFilterFlag()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(key, 1 / (1 + 0.05f * level)));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getFilterFlag()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(key));
    }
  }
}
