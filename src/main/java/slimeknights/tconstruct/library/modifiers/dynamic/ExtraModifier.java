package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/**
 * Modifier that grants a bonus modifier slot
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtraModifier extends Modifier {
  /** Loader instance */
  public static final IGenericLoader<ExtraModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ExtraModifier deserialize(JsonObject json) {
      SlotCount slots = SlotCount.fromJson(GsonHelper.getAsJsonObject(json, "slots"));
      boolean singleLevel = GsonHelper.getAsBoolean(json, "single_level", true);
      boolean alwaysShow = GsonHelper.getAsBoolean(json, "always_show", false);
      return new ExtraModifier(slots.getType(), slots.getCount(), singleLevel, alwaysShow);
    }

    @Override
    public void serialize(ExtraModifier object, JsonObject json) {
      JsonObject slots = new JsonObject();
      slots.addProperty(object.type.getName(), object.slotsPerLevel);
      json.add("slots", slots);
      json.addProperty("single_level", object.singleLevel);
      json.addProperty("always_show", object.alwaysShow);
    }

    @Override
    public ExtraModifier fromNetwork(FriendlyByteBuf buffer) {
      SlotType type = SlotType.read(buffer);
      int slotCount = buffer.readVarInt();
      boolean singleLevel = buffer.readBoolean();
      boolean alwaysShow = buffer.readBoolean();
      return new ExtraModifier(type, slotCount, singleLevel, alwaysShow);
    }

    @Override
    public void toNetwork(ExtraModifier object, FriendlyByteBuf buffer) {
      object.type.write(buffer);
      buffer.writeVarInt(object.slotsPerLevel);
      buffer.writeBoolean(object.singleLevel);
      buffer.writeBoolean(object.alwaysShow);
    }
  };

  /** Type of slot to grant */
  private final SlotType type;
  /** Slots to grant each level */
  private final int slotsPerLevel;
  /** If true, modifier hides the number at level 1 */
  private final boolean singleLevel;
  /** If false, modifier only shows in the tinker station */
  private final boolean alwaysShow;

  /** Creates a new builder */
  public static Builder builder(SlotType slotType) {
    return new Builder(slotType);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return alwaysShow || advanced;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    volatileData.addSlots(type, level * slotsPerLevel);
  }

  @Override
  public Component getDisplayName(int level) {
    if (singleLevel && level == 1) {
      return getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public int getPriority() {
    // show lower priority, the trait should be above the rest though
    return singleLevel ? 50 : 60;
  }

  /** Builder for an extra modifier */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final SlotType type;
    private boolean alwaysShow = false;
    private boolean singleLevel = true;
    @Setter
    @Accessors(fluent = true)
    private int slotsPerLevel = 1;

    /** Sets the modifier to always show, by default it only shows when in the tinker station */
    public Builder alwaysShow() {
      this.alwaysShow = true;
      return this;
    }

    /** Sets the modifier to multilevel, by default the number is hidded */
    public Builder multiLevel() {
      this.singleLevel = false;
      return this;
    }

    /** Builds a new modifier */
    public ExtraModifier build() {
      return new ExtraModifier(type, slotsPerLevel, singleLevel, alwaysShow);
    }
  }
}
