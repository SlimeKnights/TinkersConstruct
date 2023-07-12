package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for a extra slot modifier with multiple variants based on the slot type
 * @param key             Persistent data key containing the slot name. If null, uses the modifier ID.
 *                        Presently, changing this makes it incompatible with the swappable modifier recipe, this is added for future proofing.
 * @param slotCount       Number of slots to grant
 */
public record SwappableSlotModule(@Nullable ResourceLocation key, int slotCount) implements VolatileDataModifierHook, DisplayNameModifierHook, ModifierRemovalHook, ModifierModule, ModuleWithKey {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA, TinkerHooks.DISPLAY_NAME, TinkerHooks.REMOVE);
  /** Format key for swappable variant */
  private static final String FORMAT = TConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");

  public SwappableSlotModule(int slotCount) {
    this(null, slotCount);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, Modifier modifier, int level, Component name) {
    String slotName = tool.getPersistentData().getString(getKey(modifier));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        return new TranslatableComponent(FORMAT, name.plainCopy(), type.getDisplayName()).withStyle(style -> style.withColor(type.getColor()));
      }
    }
    return name;
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        volatileData.addSlots(type, slotCount);
      }
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<SwappableSlotModule> LOADER = new IGenericLoader<>() {
    @Override
    public SwappableSlotModule deserialize(JsonObject json) {
      ResourceLocation key = ModuleWithKey.parseKey(json);
      int slotCount = GsonHelper.getAsInt(json, "slots");
      return new SwappableSlotModule(key, slotCount);
    }

    @Override
    public void serialize(SwappableSlotModule object, JsonObject json) {
      if (object.key != null) {
        json.addProperty("key", object.key.toString());
      }
      json.addProperty("slots", object.slotCount);
    }

    @Override
    public SwappableSlotModule fromNetwork(FriendlyByteBuf buffer) {
      ResourceLocation key = ModuleWithKey.fromNetwork(buffer);
      int slotCount = buffer.readInt();
      return new SwappableSlotModule(key, slotCount);
    }

    @Override
    public void toNetwork(SwappableSlotModule object, FriendlyByteBuf buffer) {
      ModuleWithKey.toNetwork(object.key, buffer);
      buffer.writeInt(object.slotCount);
    }
  };

  /** Module to add (or remove) additional slots based on the given swappable slot type */
  public record BonusSlot(@Nullable ResourceLocation key, SlotType match, SlotType bonus, int slotCount) implements VolatileDataModifierHook, ModifierModule, ModuleWithKey {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);

    public BonusSlot(SlotType match, SlotType penalty, int slotCount) {
      this(null, match, penalty, slotCount);
    }

    @Override
    public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
      String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
      if (!slotName.isEmpty() && match.getName().equals(slotName)) {
        volatileData.addSlots(bonus, slotCount);
      }
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }

    public static final IGenericLoader<BonusSlot> LOADER = new IGenericLoader<>() {
      @Override
      public BonusSlot deserialize(JsonObject json) {
        ResourceLocation key = ModuleWithKey.parseKey(json);
        SlotType match = SlotType.getOrCreate(GsonHelper.getAsString(json, "match"));
        SlotType bonus = SlotType.getOrCreate(GsonHelper.getAsString(json, "bonus"));
        int slotCount = GsonHelper.getAsInt(json, "slots");
        return new BonusSlot(key, match, bonus, slotCount);
      }

      @Override
      public void serialize(BonusSlot object, JsonObject json) {
        if (object.key != null) {
          json.addProperty("key", object.key.toString());
        }
        json.addProperty("match", object.match.getName());
        json.addProperty("bonus", object.bonus.getName());
        json.addProperty("slots", object.slotCount);
      }

      @Override
      public BonusSlot fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation key = ModuleWithKey.fromNetwork(buffer);
        SlotType match = SlotType.read(buffer);
        SlotType bonus = SlotType.read(buffer);
        int slots = buffer.readInt();
        return new BonusSlot(key, match, bonus, slots);
      }

      @Override
      public void toNetwork(BonusSlot object, FriendlyByteBuf buffer) {
        ModuleWithKey.toNetwork(object.key, buffer);
        object.match.write(buffer);
        object.bonus.write(buffer);
        buffer.writeInt(object.slotCount);
      }
    };
  }
}
