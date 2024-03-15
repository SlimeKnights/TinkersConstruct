package slimeknights.tconstruct.library.json.predicate.modifier;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.SlotType;

/** Predicate that matches any modifiers with recipes requiring a slot */
public record SlotTypeModifierPredicate(SlotType slotType) implements ModifierPredicate {
  @Override
  public boolean matches(ModifierId input) {
    return ModifierRecipeLookup.isRecipeModifier(slotType, input);
  }

  @Override
  public IGenericLoader<SlotTypeModifierPredicate> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<SlotTypeModifierPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public SlotTypeModifierPredicate deserialize(JsonObject json) {
      SlotType slotType = null;
      if (json.has("slot")) {
        slotType = SlotType.getOrCreate(GsonHelper.getAsString(json, "slot"));
      }
      return new SlotTypeModifierPredicate(slotType);
    }

    @Override
    public SlotTypeModifierPredicate fromNetwork(FriendlyByteBuf buffer) {
      SlotType slotType = null;
      if (buffer.readBoolean()) {
        slotType = SlotType.read(buffer);
      }
      return new SlotTypeModifierPredicate(slotType);
    }

    @Override
    public void serialize(SlotTypeModifierPredicate object, JsonObject json) {
      if (object.slotType != null) {
        json.addProperty("slot", object.slotType.getName());
      }
    }

    @Override
    public void toNetwork(SlotTypeModifierPredicate object, FriendlyByteBuf buffer) {
      if (object.slotType != null) {
        buffer.writeBoolean(true);
        object.slotType.write(buffer);
      } else {
        buffer.writeBoolean(false);
      }
    }
  };
}
