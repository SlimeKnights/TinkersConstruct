package slimeknights.tconstruct.library.tools.definition.module.display;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Tool name singleton implementations */
public enum SimpleToolName implements ToolModule, ToolNameHook.FromDefault {
  /** Displays the tool using the simple translation of the item, ignoring materials. */
  ITEM {
    @Override
    public Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool, Component name) {
      // this one is kinda pointless if you are composing it. So I guess don't?
      return name;
    }
  };

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SimpleToolName>defaultHooks(ToolHooks.DISPLAY_NAME);
  @Getter
  private final RecordLoadable<SimpleToolName> loader = new SingletonLoader<>(this);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
