package slimeknights.tconstruct.library.modifiers.modules.fluid;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

/** Module letting a tool change tank capacity */
@Getter
@RequiredArgsConstructor
public class TankCapacityModule implements ModifierModule, VolatileDataModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);
  /** Default key for capacity */
  public static final ResourceLocation DEFAULT_CAPACITY_KEY = TConstruct.getResource("tank_capacity");

  /** Volatile NBT integer indicating the tank's max capacity */
  private final ResourceLocation capacityKey;
  /** Max capacity added by this module */
  private final int capacity;
  /** If true, capacity scales with level */
  private final boolean scaleCapacity;

  public TankCapacityModule(int capacity, boolean scaleCapacity) {
    this(DEFAULT_CAPACITY_KEY, capacity, scaleCapacity);
  }

  /** Gets the full capacity of the tank */
  public int getCapacity(IToolStackView tool) {
    return tool.getVolatileData().getInt(getCapacityKey());
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    ResourceLocation key = getCapacityKey();
    volatileData.putInt(key, capacity * modifier.getLevel() + volatileData.getInt(key));
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<TankCapacityModule> LOADER = new IGenericLoader<>() {
    @Override
    public TankCapacityModule deserialize(JsonObject json) {
      int capacity = GsonHelper.getAsInt(json, "capacity");
      boolean scaleCapacity = GsonHelper.getAsBoolean(json, "scale_capacity");
      ResourceLocation capacityKey = JsonHelper.getResourceLocation(json, "capacity_key", DEFAULT_CAPACITY_KEY);
      return new TankCapacityModule(capacityKey, capacity, scaleCapacity);
    }

    @Override
    public void serialize(TankCapacityModule object, JsonObject json) {
      json.addProperty("capacity", object.capacity);
      json.addProperty("scale_capacity", object.scaleCapacity);
      if (object.capacityKey != DEFAULT_CAPACITY_KEY) {
        json.addProperty("capacity_key", object.capacityKey.toString());
      }
    }

    @Override
    public TankCapacityModule fromNetwork(FriendlyByteBuf buffer) {
      ResourceLocation capacityKey = buffer.readResourceLocation();
      int capacity = buffer.readVarInt();
      boolean scaleCapacity = buffer.readBoolean();
      return new TankCapacityModule(capacityKey, capacity, scaleCapacity);
    }

    @Override
    public void toNetwork(TankCapacityModule object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.capacityKey);
      buffer.writeVarInt(object.capacity);
      buffer.writeBoolean(object.scaleCapacity);
    }
  };
}
