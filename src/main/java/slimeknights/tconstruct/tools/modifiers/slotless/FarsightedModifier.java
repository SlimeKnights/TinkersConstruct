package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Arrays;
import java.util.Comparator;

public class FarsightedModifier extends IncrementalModifier {
  private final ResourceLocation[] SLOT_KEYS = Arrays.stream(EquipmentSlotType.values())
                                                     .sorted(Comparator.comparing(EquipmentSlotType::getSlotIndex))
                                                     .map(slot -> TConstruct.getResource("farsighted_" + slot.getName()))
                                                     .toArray(ResourceLocation[]::new);
  public FarsightedModifier() {
    super(0x796571);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getSlotIndex()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(key, 1 / (1 + 0.05f * level)));
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    EquipmentSlotType slot = context.getChangedSlot();
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getSlotIndex()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(key));
    }
  }
}
