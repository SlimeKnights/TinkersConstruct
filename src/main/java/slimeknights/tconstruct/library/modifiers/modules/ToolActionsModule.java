package slimeknights.tconstruct.library.modifiers.modules;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ToolActionModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/**
 * Module that allows a modifier to perform tool actions
 */
public record ToolActionsModule(Set<ToolAction> actions) implements ToolActionModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_ACTION);

  public ToolActionsModule(ToolAction... actions) {
    this(ImmutableSet.copyOf(actions));
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return actions.contains(toolAction);
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  public static final IGenericLoader<ToolActionsModule> LOADER = new IGenericLoader<ToolActionsModule>() {
    @Override
    public ToolActionsModule deserialize(JsonObject json) {
      return new ToolActionsModule(ImmutableSet.copyOf(JsonHelper.parseList(
        json, "tool_actions",
        (element, name) -> ToolAction.get(GsonHelper.convertToString(element, name))
      )));
    }

    @Override
    public ToolActionsModule fromNetwork(FriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      ImmutableSet.Builder<ToolAction> actions = ImmutableSet.builder();
      for (int i = 0; i < size; i++) {
        actions.add(ToolAction.get(buffer.readUtf(Short.MAX_VALUE)));
      }
      return new ToolActionsModule(actions.build());
    }

    @Override
    public void serialize(ToolActionsModule object, JsonObject json) {
      JsonArray actions = new JsonArray();
      for (ToolAction action : object.actions) {
        actions.add(action.name());
      }
      json.add("actions", actions);
    }

    @Override
    public void toNetwork(ToolActionsModule object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.actions.size());
      for (ToolAction action : object.actions) {
        buffer.writeUtf(action.name());
      }
    }
  };
}
