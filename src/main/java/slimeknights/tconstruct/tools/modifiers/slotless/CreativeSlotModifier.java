package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.ArrayList;
import java.util.List;

/** Modifier that adds a variable number of slots to a tool. Could easily be done via NBT editing, but this makes it easier */
public class CreativeSlotModifier extends SingleUseModifier {
  /** Key representing the slots object in the modifier */
  public static final ResourceLocation KEY_SLOTS = TConstruct.getResource("creative");

  public CreativeSlotModifier() {
    super(0xCCBA47);
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    tool.getPersistentData().remove(KEY_SLOTS);
  }

  @Override
  public void addVolatileData(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    if (persistentData.contains(KEY_SLOTS, NBT.TAG_COMPOUND)) {
      CompoundNBT slots = persistentData.getCompound(KEY_SLOTS);
      for (String key : slots.keySet()) {
        SlotType slotType = SlotType.getIfPresent(key);
        if (slotType != null) {
          volatileData.addSlots(slotType, slots.getInt(key));
        }
      }
    }
  }

  /** Formats the given slot type as a count */
  private static ITextComponent formatCount(SlotType slotType, int count) {
    return new StringTextComponent((count > 0 ? "+" : "") + count + " ")
      .appendSibling(slotType.getDisplayName())
      .modifyStyle(style -> style.setColor(slotType.getColor()));
  }

  @Override
  public List<ITextComponent> getDescriptionList(IModifierToolStack tool, int level) {
    List<ITextComponent> tooltip = getDescriptionList(level);
    IModDataReadOnly persistentData = tool.getPersistentData();
    if (persistentData.contains(KEY_SLOTS, NBT.TAG_COMPOUND)) {
      CompoundNBT slots = persistentData.getCompound(KEY_SLOTS);

      // first one found has special behavior
      boolean first = true;
      for (String key : slots.keySet()) {
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
