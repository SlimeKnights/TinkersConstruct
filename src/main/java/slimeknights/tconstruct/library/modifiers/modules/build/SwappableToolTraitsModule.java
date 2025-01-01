package slimeknights.tconstruct.library.modifiers.modules.build;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule.FORMAT;

/** Module to add additional traits to a tool given the passed hook */
public class SwappableToolTraitsModule implements ModifierModule, ModifierTraitHook, DisplayNameModifierHook, ModifierRemovalHook, ModuleWithKey {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SwappableToolTraitsModule>defaultHooks(ModifierHooks.MODIFIER_TRAITS, ModifierHooks.DISPLAY_NAME, ModifierHooks.REMOVE);
  @SuppressWarnings("unchecked")
  public static final RecordLoadable<SwappableToolTraitsModule> LOADER = RecordLoadable.create(
    ModuleWithKey.FIELD,
    StringLoadable.DEFAULT.requiredField("match", m -> m.match),
    ToolHooks.LOADER.comapFlatMap((hook, error) -> {
      if (!hook.supportsHook(ToolTraitHook.class)) {
        throw error.create("Hook " + hook.getId() + " is not valid for ToolTraitHook");
      }
      return (ModuleHook<ToolTraitHook>) hook;
    }, hook -> hook).requiredField("hook", m -> m.hook),
    SwappableToolTraitsModule::new);

  @Nullable
  @Getter @Accessors(fluent = true)
  private final ResourceLocation key;
  private final String match;
  private final ModuleHook<ToolTraitHook> hook;
  private final Component component;

  public SwappableToolTraitsModule(@Nullable ResourceLocation key, String match, ModuleHook<ToolTraitHook> hook) {
    this.key = key;
    this.match = match;
    this.hook = hook;
    this.component = Component.translatable(SlotType.KEY_DISPLAY + match);
  }

  @Override
  public RecordLoadable<? extends SwappableToolTraitsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    if (match.equals(tool.getPersistentData().getString(getKey(entry.getModifier())))) {
      return Component.translatable(FORMAT, name.plainCopy(), component).withStyle(name.getStyle());
    }
    return name;
  }


  @Override
  public void addTraits(IToolContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter) {
    if (match.equals(context.getPersistentData().getString(getKey(modifier.getModifier())))) {
      ToolDefinition definition = context.getDefinition();
      definition.getHook(hook).addTraits(definition, context.getMaterials(), builder);
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getKey(modifier));
    return null;
  }
}
