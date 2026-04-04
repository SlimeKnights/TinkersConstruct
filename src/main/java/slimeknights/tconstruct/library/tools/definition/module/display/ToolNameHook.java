package slimeknights.tconstruct.library.tools.definition.module.display;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.Collection;

/** Hook for fetching the display name of a tool */
public interface ToolNameHook {
  /**
   * Gets the display name for the given tool
   * @param definition  Tool definition instance
   * @param stack       Stack instance
   * @param tool        Tool stack instance, may be null indicating you may create it from {@code stack}
   * @return  Display name component
   * @deprecated use {@link #getDisplayName(ToolDefinition, ItemStack, IToolStackView, Component)}. Calling is okay.
   * @see FromDefault
   */
  @Deprecated
  Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool);

  /**
   * Gets the display name for the given tool
   * @param definition  Tool definition instance
   * @param stack       Item stack instance for calling the legacy hook.
   * @param tool        Tool instance, for fetching either stack or tool.
   * @param name        Name from previous hooks, allowing composing.
   * @return  Display name component
   */
  default Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool, Component name) {
    return getDisplayName(definition, stack, tool);
  }

  /** Gets the tool for the parameters */
  static IToolStackView getTool(ItemStack stack, @Nullable IToolStackView tool) {
    if (tool != null) {
      return tool;
    }
    return ToolStack.from(stack);
  }

  /**
   * Gets the display name for a tool including as defined by the hook.
   * @param definition  Tool definition
   * @param stack       Stack instance
   * @param tool        Tool instance, may be null if one does not exist
   * @return  Display name including the head material
   */
  static Component getName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool) {
    // support override name
    String name = TooltipUtil.getDisplayName(stack);
    if (!name.isEmpty()) {
      return Component.literal(name);
    }
    return definition.getHook(ToolHooks.DISPLAY_NAME).getDisplayName(definition, stack, tool);
  }

  /**
   * Gets the display name for a tool including as defined by the hook.
   * @param definition  Tool definition
   * @param stack       Stack instance
   * @return  Display name including the head material
   */
  static Component getName(ToolDefinition definition, ItemStack stack) {
    return getName(definition, stack, null);
  }

  /** Helper for implementing the new method on the interface */
  interface FromDefault extends ToolNameHook {
    @Override
    Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool, Component name);

    @Override
    default Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool) {
      return getDisplayName(definition, stack, tool, Component.translatable(stack.getItem().getDescriptionId()));
    }
  }

  /** Merger running each hook in order */
  record ComposeMerger(Collection<ToolNameHook> modules) implements ToolNameHook {
    @Override
    public Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool) {
      // create the tool instance so we can share it between multiple implementations
      return getDisplayName(definition, stack, getTool(stack, tool), Component.translatable(stack.getItem().getDescriptionId()));
    }

    @Override
    public Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool, Component name) {
      for (ToolNameHook hook : modules) {
        name = hook.getDisplayName(definition, stack, tool, name);
      }
      return name;
    }
  }
}
