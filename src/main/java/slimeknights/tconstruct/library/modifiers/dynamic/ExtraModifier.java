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
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/** @deprecated use {@link ModifierSlotModule} */
@Deprecated
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtraModifier extends Modifier {
  /** Loader instance */
  public static final IGenericLoader<ExtraModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ExtraModifier deserialize(JsonObject json) {
      SlotCount slots = SlotCount.fromJson(GsonHelper.getAsJsonObject(json, "slots"));
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.getAndDeserialize(json, "level_display");
      boolean alwaysShow = GsonHelper.getAsBoolean(json, "always_show", false);
      return new ExtraModifier(slots.getType(), slots.getCount(), display, alwaysShow);
    }

    @Override
    public void serialize(ExtraModifier object, JsonObject json) {
      JsonObject slots = new JsonObject();
      json.add("level_display", ModifierLevelDisplay.LOADER.serialize(object.levelDisplay));
      json.addProperty("always_show", object.alwaysShow);
      slots.addProperty(object.type.getName(), object.slotsPerLevel);
      json.add("slots", slots);
    }

    @Override
    public ExtraModifier fromNetwork(FriendlyByteBuf buffer) {
      SlotType type = SlotType.read(buffer);
      int slotCount = buffer.readVarInt();
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.fromNetwork(buffer);
      boolean alwaysShow = buffer.readBoolean();
      return new ExtraModifier(type, slotCount, display, alwaysShow);
    }

    @Override
    public void toNetwork(ExtraModifier object, FriendlyByteBuf buffer) {
      object.type.write(buffer);
      buffer.writeVarInt(object.slotsPerLevel);
      ModifierLevelDisplay.LOADER.toNetwork(object.levelDisplay, buffer);
      buffer.writeBoolean(object.alwaysShow);
    }
  };

  /** Type of slot to grant */
  private final SlotType type;
  /** Slots to grant each level */
  private final int slotsPerLevel;
  /** Formatter for level display */
  private final ModifierLevelDisplay levelDisplay;
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
    return levelDisplay.nameForLevel(this, level);
  }

  @Override
  public int getPriority() {
    // show lower priority so they group together, if it always shows put it a bit higher
    return alwaysShow ? 60 : 50;
  }

  /** Builder for an extra modifier */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  public static class Builder {
    private final SlotType type;
    private boolean alwaysShow = false;
    @Setter
    private ModifierLevelDisplay display = ModifierLevelDisplay.SINGLE_LEVEL;
    @Setter
    private int slotsPerLevel = 1;

    /** Sets the modifier to always show, by default it only shows when in the tinker station */
    public Builder alwaysShow() {
      this.alwaysShow = true;
      return this;
    }

    /** Builds a new modifier */
    public ExtraModifier build() {
      return new ExtraModifier(type, slotsPerLevel, display, alwaysShow);
    }
  }
}
