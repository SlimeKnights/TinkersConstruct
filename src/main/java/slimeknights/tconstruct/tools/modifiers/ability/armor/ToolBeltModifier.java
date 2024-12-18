package slimeknights.tconstruct.tools.modifiers.ability.armor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.INamespacedNBTView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.modules.armor.ToolBeltModule;

import javax.annotation.Nullable;
import java.util.Set;

/** @deprecated use {@link ToolBeltModule}, {@link slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule}, and {@link InventoryMenuModule} */
@SuppressWarnings({"DeprecatedIsStillUsed", "removal"})
@Deprecated(forRemoval = true)
public class ToolBeltModifier extends InventoryModifier implements VolatileDataModifierHook {
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "tool_belt");
  private static final ResourceLocation SLOT_OVERRIDE = TConstruct.getResource("tool_belt_override");

  /** Loader instance */
  public static final IGenericLoader<ToolBeltModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ToolBeltModifier deserialize(JsonObject json) {
      JsonArray slotJson = GsonHelper.getAsJsonArray(json, "level_slots");
      int[] slots = new int[slotJson.size()];
      // TODO: can this sort of thing be generalized?
      for (int i = 0; i < slots.length; i++) {
        slots[i] = GsonHelper.convertToInt(slotJson.get(i), "level_slots["+i+"]");
        if (i > 0 && slots[i] <= slots[i-1]) {
          throw new JsonSyntaxException("level_slots must be increasing");
        }
      }
      return new ToolBeltModifier(slots);
    }

    @Override
    public ToolBeltModifier fromNetwork(FriendlyByteBuf buffer) {
      return new ToolBeltModifier(buffer.readVarIntArray());
    }

    @Override
    public void serialize(ToolBeltModifier object, JsonObject json) {
      JsonArray jsonArray = new JsonArray();
      for (int i : object.counts) {
        jsonArray.add(i);
      }
      json.add("level_slots", jsonArray);
    }

    @Override
    public void toNetwork(ToolBeltModifier object, FriendlyByteBuf buffer) {
      buffer.writeVarIntArray(object.counts);
    }
  };

  private final int[] counts;
  public ToolBeltModifier(int[] counts) {
    super(counts[0]);
    this.counts = counts;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new ToolBeltModule(Set.of(TooltipKey.NORMAL, TooltipKey.CONTROL)));
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    hookBuilder.addModule(InventoryMenuModule.SHIFT);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public Component getDisplayName(int level) {
    return ModifierLevelDisplay.PLUSES.nameForLevel(this, level);
  }

  @Override
  public int getPriority() {
    return 85; // after shield strap, before pockets
  }

  /** Gets the proper number of slots for the given level */
  private int getProperSlots(ModifierEntry entry) {
    int level = entry.intEffectiveLevel();
    if (level <= 0) {
      return 0;
    }
    if (level > counts.length) {
      return 9;
    } else {
      return counts[level - 1];
    }
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    int properSlots = getProperSlots(modifier);
    int slots;
    // find the largest slot index and either add or update the override as needed
    // TODO: can probably remove this code for 1.19
    if (properSlots < 9) {
      slots = properSlots;
      ResourceLocation key = getInventoryKey();
      IModDataView modData = context.getPersistentData();
      if (modData.contains(key, Tag.TAG_LIST)) {
        ListTag list = modData.get(key, GET_COMPOUND_LIST);
        int maxSlot = 0;
        for (int i = 0; i < list.size(); i++) {
          int newSlot = list.getCompound(i).getInt(TAG_SLOT);
          if (newSlot > maxSlot) {
            maxSlot = newSlot;
          }
        }
        maxSlot = Math.min(maxSlot + 1, 9);
        if (maxSlot > properSlots) {
          volatileData.putInt(SLOT_OVERRIDE, maxSlot);
          slots = maxSlot;
        }
      }
    } else {
      slots = 9;
    }
    ToolInventoryCapability.addSlots(volatileData, slots);
  }

  @Override
  public int getSlots(INamespacedNBTView volatileData, ModifierEntry modifier) {
    int properSlots = getProperSlots(modifier);
    if (properSlots >= 9) {
      return 9;
    }
    return Mth.clamp(volatileData.getInt(SLOT_OVERRIDE), properSlots, 9);
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    return validateForMaxSlots(tool, getProperSlots(modifier));
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : PATTERN;
  }
}
