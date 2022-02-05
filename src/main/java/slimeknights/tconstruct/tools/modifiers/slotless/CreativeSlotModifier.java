package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.ArrayList;
import java.util.List;

/** Modifier that adds a variable number of slots to a tool. Could easily be done via Tag editing, but this makes it easier */
public class CreativeSlotModifier extends SingleUseModifier {
  /** Key representing the slots object in the modifier */
  public static final ResourceLocation KEY_SLOTS = TConstruct.getResource("creative");

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(KEY_SLOTS);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
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
    return new TextComponent((count > 0 ? "+" : "") + count + " ")
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
