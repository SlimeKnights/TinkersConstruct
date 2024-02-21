package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.List;

/**
 * Module that adds extra modifier slots to a tool.
 */
public record ModifierSlotModule(SlotType type, int count, ModifierModuleCondition condition) implements VolatileDataModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);

  public ModifierSlotModule(SlotType type, int count) {
    this(type, count, ModifierModuleCondition.ANY);
  }

  public ModifierSlotModule(SlotType type) {
    this(type, 1);
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      volatileData.addSlots(type, count * modifier.getLevel());
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Loader instance */
  public static final IGenericLoader<ModifierSlotModule> LOADER = new IGenericLoader<>() {
    @Override
    public ModifierSlotModule deserialize(JsonObject json) {
      String slotName = GsonHelper.getAsString(json, "name");
      if (!SlotType.isValidName(slotName)) {
        throw new JsonSyntaxException("Invalid slot type name '" + slotName + "'");
      }
      int count = JsonUtils.getIntMin(json, "count", 1);
      return new ModifierSlotModule(SlotType.getOrCreate(slotName), count);
    }

    @Override
    public void serialize(ModifierSlotModule object, JsonObject json) {
      json.addProperty("name", object.type.getName());
      json.addProperty("count", object.count);
    }

    @Override
    public ModifierSlotModule fromNetwork(FriendlyByteBuf buffer) {
      SlotType type = SlotType.read(buffer);
      int slotCount = buffer.readVarInt();
      return new ModifierSlotModule(type, slotCount);
    }

    @Override
    public void toNetwork(ModifierSlotModule object, FriendlyByteBuf buffer) {
      object.type.write(buffer);
      buffer.writeVarInt(object.count);
    }
  };
}
