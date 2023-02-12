package slimeknights.tconstruct.library.tools.definition.module.interaction;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Locale;

/**
 * Interaction that makes only a limited set work in the preferred hand, the rest working in the other hand
 */
public record PreferenceSetInteraction(InteractionSource preferredSource, IJsonPredicate<ModifierId> preferenceModifiers) implements InteractionToolModule, IToolModule {
  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == preferredSource) == preferenceModifiers.matches(modifier);
  }

  @Override
  public IGenericLoader<? extends IToolModule> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<PreferenceSetInteraction> LOADER = new IGenericLoader<>() {
    @Override
    public PreferenceSetInteraction deserialize(JsonObject json) {
      InteractionSource preferredSource = JsonHelper.getAsEnum(json, "preferred_source", InteractionSource.class);
      IJsonPredicate<ModifierId> preferenceModifiers = ModifierPredicate.LOADER.getAndDeserialize(json, "preferred_modifiers");
      return new PreferenceSetInteraction(preferredSource, preferenceModifiers);
    }

    @Override
    public PreferenceSetInteraction fromNetwork(FriendlyByteBuf buffer) {
      InteractionSource preferredSource = buffer.readEnum(InteractionSource.class);
      IJsonPredicate<ModifierId> preferenceModifiers = ModifierPredicate.LOADER.fromNetwork(buffer);
      return new PreferenceSetInteraction(preferredSource, preferenceModifiers);
    }

    @Override
    public void serialize(PreferenceSetInteraction object, JsonObject json) {
      json.addProperty("preferred_source", object.preferredSource.toString().toLowerCase(Locale.ROOT));
      json.add("preferred_modifiers", ModifierPredicate.LOADER.serialize(object.preferenceModifiers));
    }

    @Override
    public void toNetwork(PreferenceSetInteraction object, FriendlyByteBuf buffer) {
      buffer.writeEnum(object.preferredSource);
      ModifierPredicate.LOADER.toNetwork(object.preferenceModifiers, buffer);
    }
  };
}
