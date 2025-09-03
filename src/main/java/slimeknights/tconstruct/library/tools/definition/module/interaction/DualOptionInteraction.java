package slimeknights.tconstruct.library.tools.definition.module.interaction;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Tool that supports interaction with either hand. Uses persistent NBT to choose which hand is allowed to interact */
public enum DualOptionInteraction implements InteractionToolModule, ToolModule {
  /** Singleton instance */
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DualOptionInteraction>defaultHooks(ToolHooks.INTERACTION);
  /** Loader instance */
  public static final SingletonLoader<DualOptionInteraction> LOADER = new SingletonLoader<>(INSTANCE);
  /** @deprecated use {@link InteractionSource#getKey()} */
  @Deprecated(forRemoval = true)
  public static final ResourceLocation KEY = InteractionSource.LEFT_CLICK.getKey();

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == InteractionSource.RIGHT_CLICK) != ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), KEY, modifier);
  }

  @Override
  public SingletonLoader<DualOptionInteraction> getLoader() {
    return LOADER;
  }

  /** @deprecated use {@link InteractionSource#formatModifierName(IToolStackView, Modifier, Component)} */
  @Deprecated(forRemoval = true)
  public static Component formatModifierName(IToolStackView tool, Modifier modifier, Component originalName) {
    return InteractionSource.formatModifierName(tool, modifier, originalName);
  }
}
