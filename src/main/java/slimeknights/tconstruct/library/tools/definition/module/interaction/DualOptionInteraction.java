package slimeknights.tconstruct.library.tools.definition.module.interaction;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Tool that supports interaction with either hand. Uses persistent NBT to choose which hand is allowed to interact */
public class DualOptionInteraction implements InteractionToolModule, IToolModule {
  /** Singleton instance */
  public static final DualOptionInteraction INSTANCE = new DualOptionInteraction();
  /** Loader instance */
  public static final SingletonLoader<DualOptionInteraction> LOADER = new SingletonLoader<>(INSTANCE);
  /** Key for persistent data set of modifiers */
  public static final ResourceLocation KEY = TConstruct.getResource("attack_modifiers");
  /** Key for denoting this feature in the tooltip */
  private static final String MODIFIER_FORMAT = TConstruct.makeTranslationKey("modifier", "attack_toggled");

  private DualOptionInteraction() {}

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == InteractionSource.RIGHT_CLICK) == ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), KEY, modifier);
  }

  @Override
  public IGenericLoader<? extends IToolModule> getLoader() {
    return LOADER;
  }

  /** Adds the format string to the modifier name */
  public static Component formatModifierName(IToolStackView tool, Modifier modifier, Component originalName) {
    if (ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), KEY, modifier.getId())) {
      return modifier.applyStyle(new TranslatableComponent(MODIFIER_FORMAT, originalName));
    }
    return originalName;
  }
}
