package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Modifier that grants slot type based on an NBT tag
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SwappableExtraSlotModifier extends NoLevelsModifier {
  /** Format key for swappable variant */
  private static final String FORMAT = TConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");

  /** Slots to grant each level */
  private final int slots;
  /** If false, modifier only shows in the tinker station */
  private final boolean alwaysShow;
  /** Map of slot types to penalize */
  private final Map<SlotType,SlotType> penalize;

  /** Creates a new builder for a swappable modifier */
  public static Builder swappable() {
    return new Builder();
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return alwaysShow || advanced;
  }

  @Override
  public int getPriority() {
    // show lower priority so they group together, if it always shows put it a bit higher
    return alwaysShow ? 60 : 50;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    Component name = super.getDisplayName(tool, level);
    String slotName = tool.getPersistentData().getString(getId());
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        name = new TranslatableComponent(FORMAT, name.plainCopy(), type.getDisplayName()).withStyle(style -> style.withColor(type.getColor()));
      }
    }
    return name;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    String slotName = context.getPersistentData().getString(getId());
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        volatileData.addSlots(type, slots);
        SlotType penalty = penalize.get(type);
        if (penalty != null) {
          volatileData.addSlots(penalty, -slots);
        }
      }
    }
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Builder for an extra modifier */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  public static class Builder {
    private final ImmutableMap.Builder<SlotType,SlotType> penalize = ImmutableMap.builder();
    private boolean alwaysShow = false;
    @Setter
    private int slotsPerLevel = 1;

    /** Sets the modifier to always show, by default it only shows when in the tinker station */
    public Builder alwaysShow() {
      this.alwaysShow = true;
      return this;
    }

    /** Adds the given penalty slot when the given type is added */
    public Builder penalize(SlotType added, SlotType penalty) {
      penalize.put(added, penalty);
      return this;
    }

    /** Builds a new modifier */
    public SwappableExtraSlotModifier build() {
      return new SwappableExtraSlotModifier(slotsPerLevel, alwaysShow, penalize.build());
    }
  }

  /** Loader instance */
  public static final IGenericLoader<SwappableExtraSlotModifier> LOADER = new IGenericLoader<>() {
    @Override
    public SwappableExtraSlotModifier deserialize(JsonObject json) {
      int slotCount = GsonHelper.getAsInt(json, "slots");
      Map<SlotType,SlotType> penalties = Collections.emptyMap();
      if (json.has("penalize")) {
        ImmutableMap.Builder<SlotType,SlotType> map = ImmutableMap.builder();
        for (Entry<String,JsonElement> entry : GsonHelper.getAsJsonObject(json, "penalize").entrySet()) {
          String key = entry.getKey();
          SlotType result = SlotType.getOrCreate(GsonHelper.convertToString(entry.getValue(), "penalize[" + entry.getKey() + ']'));
          map.put(SlotType.getOrCreate(key), result);
        }
        penalties = map.build();
      }
      boolean alwaysShow = GsonHelper.getAsBoolean(json, "always_show", false);
      return new SwappableExtraSlotModifier(slotCount, alwaysShow, penalties);
    }

    @Override
    public void serialize(SwappableExtraSlotModifier object, JsonObject json) {
      json.addProperty("slots", object.slots);
      json.addProperty("always_show", object.alwaysShow);
      if (!object.penalize.isEmpty()) {
        JsonObject penaltyJson = new JsonObject();
        for (Entry<SlotType,SlotType> entry : object.penalize.entrySet()) {
          penaltyJson.addProperty(entry.getKey().getName(), entry.getValue().getName());
        }
        json.add("penalize", penaltyJson);
      }
    }

    @Override
    public SwappableExtraSlotModifier fromNetwork(FriendlyByteBuf buffer) {
      int slotCount = buffer.readVarInt();
      boolean alwaysShow = buffer.readBoolean();
      ImmutableMap.Builder<SlotType,SlotType> penalize = ImmutableMap.builder();
      int size = buffer.readVarInt();
      for (int i = 0; i < size; i++) {
        penalize.put(SlotType.read(buffer), SlotType.read(buffer));
      }
      return new SwappableExtraSlotModifier(slotCount, alwaysShow, penalize.build());
    }

    @Override
    public void toNetwork(SwappableExtraSlotModifier object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.slots);
      buffer.writeBoolean(object.alwaysShow);
      buffer.writeVarInt(object.penalize.size());
      for (Entry<SlotType,SlotType> entry : object.penalize.entrySet()) {
        entry.getKey().write(buffer);
        entry.getValue().write(buffer);
      }
    }
  };
}
