package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule.ModuleWithHooks;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;

import java.util.Collections;
import java.util.List;

/**
 * Modifier consisting of many composed hooks
 */
public class ComposableModifier extends Modifier {
  private final ModifierLevelDisplay levelDisplay;
  private final List<ModuleWithHooks> modules;
  public ComposableModifier(ModifierLevelDisplay levelDisplay, List<ModuleWithHooks> modules) {
    super(ModifierModule.createMap(modules));
    this.levelDisplay = levelDisplay;
    this.modules = modules;
  }

  /** Creates a builder instance for datagen */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** This method is final to prevent overrides as the constructor no longer calls it */
  @Override
  protected final void registerHooks(ModifierHookMap.Builder hookBuilder) {}

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
  }

  public static IGenericLoader<ComposableModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ComposableModifier deserialize(JsonObject json) {
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.getAndDeserialize(json, "level_display");
      List<ModuleWithHooks> modules = JsonHelper.parseList(json, "modules", ModuleWithHooks::deserialize);
      // convert illegal argument to json syntax, bit more expected in this context
      try {
        return new ComposableModifier(display, modules);
      } catch (IllegalArgumentException e) {
        throw new JsonSyntaxException(e.getMessage(), e);
      }
    }

    @Override
    public void serialize(ComposableModifier object, JsonObject json) {
      json.add("level_display", ModifierLevelDisplay.LOADER.serialize(object.levelDisplay));
      JsonArray modules = new JsonArray();
      for (ModuleWithHooks module : object.modules) {
        modules.add(module.serialize());
      }
      json.add("modules", modules);
    }

    @Override
    public ComposableModifier fromNetwork(FriendlyByteBuf buffer) {
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.fromNetwork(buffer);
      int moduleCount = buffer.readVarInt();
      ImmutableList.Builder<ModuleWithHooks> builder = ImmutableList.builder();
      for (int i = 0; i < moduleCount; i++) {
        builder.add(ModuleWithHooks.fromNetwork(buffer));
      }
      try {
        return new ComposableModifier(display, builder.build());
      } catch (IllegalArgumentException e) {
        throw new DecoderException(e.getMessage(), e);
      }
    }

    @Override
    public void toNetwork(ComposableModifier object, FriendlyByteBuf buffer) {
      ModifierLevelDisplay.LOADER.toNetwork(object.levelDisplay, buffer);
      buffer.writeVarInt(object.modules.size());
      for (ModuleWithHooks module : object.modules) {
        module.toNetwork(buffer);
      }
    }
  };

  /** Builder for a composable modifier instance */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  public static class Builder {
    @Setter
    private ModifierLevelDisplay levelDisplay = ModifierLevelDisplay.DEFAULT;
    private final ImmutableList.Builder<ModuleWithHooks> modules = ImmutableList.builder();

    /** Adds a module to the builder */
    public final <T extends ModifierModule> Builder addHook(T object) {
      modules.add(new ModuleWithHooks(object, Collections.emptyList()));
      return this;
    }

    /** Adds a module to the builder */
    @SafeVarargs
    public final <T extends ModifierModule> Builder addHook(T object, ModifierHook<? super T>... hooks) {
      modules.add(new ModuleWithHooks(object, List.of(hooks)));
      return this;
    }

    /** Builds the final instance */
    public ComposableModifier build() {
      return new ComposableModifier(levelDisplay, modules.build());
    }
  }
}
