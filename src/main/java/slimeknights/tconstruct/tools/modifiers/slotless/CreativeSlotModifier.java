package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.ArrayList;
import java.util.List;

/** Modifier that adds a variable number of slots to a tool. Could easily be done via Tag editing, but this makes it easier */
public class CreativeSlotModifier extends NoLevelsModifier implements VolatileDataModifierHook, ModifierRemovalHook {
  /** Key representing the slots object in the modifier */
  public static final ResourceLocation KEY_SLOTS = TConstruct.getResource("creative");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.VOLATILE_DATA, TinkerHooks.REMOVE);
  }

  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(KEY_SLOTS);
    return null;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    IModDataView persistentData = context.getPersistentData();
    if (persistentData.contains(KEY_SLOTS, Tag.TAG_COMPOUND)) {
      CompoundTag slots = persistentData.getCompound(KEY_SLOTS);
      for (String key : slots.getAllKeys()) {
        SlotType slotType = SlotType.getIfPresent(key);
        if (slotType != null) {
          volatileData.addSlots(slotType, slots.getInt(key));
        }
      }
    }
  }

  /** Formats the given slot type as a count */
  private static Component formatCount(SlotType slotType, int count) {
    return Component.literal((count > 0 ? "+" : "") + count + " ")
      .append(slotType.getDisplayName())
      .withStyle(style -> style.withColor(slotType.getColor()));
  }

  @Override
  public List<Component> getDescriptionList(IToolStackView tool, int level) {
    List<Component> tooltip = getDescriptionList(level);
    IModDataView persistentData = tool.getPersistentData();
    if (persistentData.contains(KEY_SLOTS, Tag.TAG_COMPOUND)) {
      CompoundTag slots = persistentData.getCompound(KEY_SLOTS);

      // first one found has special behavior
      boolean first = true;
      for (String key : slots.getAllKeys()) {
        SlotType slotType = SlotType.getIfPresent(key);
        if (slotType != null) {
          if (first) {
            // found a valid slot? copy the list once then add the rest
            tooltip = new ArrayList<>(tooltip);
            first = false;
          }
          tooltip.add(formatCount(slotType, slots.getInt(key)));
        }
      }
    }
    return tooltip;
  }
}
