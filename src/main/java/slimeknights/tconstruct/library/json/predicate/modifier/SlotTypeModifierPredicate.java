package slimeknights.tconstruct.library.json.predicate.modifier;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.SlotType;

import javax.annotation.Nullable;

/** Predicate that matches any modifiers with recipes requiring a slot */
public record SlotTypeModifierPredicate(@Nullable SlotType slotType) implements ModifierPredicate {
  public static final RecordLoadable<SlotTypeModifierPredicate> LOADER = RecordLoadable.create(SlotType.LOADABLE.nullableField("slot", SlotTypeModifierPredicate::slotType), SlotTypeModifierPredicate::new);

  @Override
  public boolean matches(ModifierId input) {
    return ModifierRecipeLookup.isRecipeModifier(slotType, input);
  }

  @Override
  public RecordLoadable<SlotTypeModifierPredicate> getLoader() {
    return LOADER;
  }
}
