package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.item.Item;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

/** Modifier for compatability with TOP */
public class TOPModifier extends SingleUseModifier {
  private static final String TOP_NBT_HELMET = "theoneprobe";
  private static final String TOP_NBT_HAND = "theoneprobe_hand";
  public TOPModifier() {
    super(0x7FF2FF);
  }

  @Override
  public void addRawData(IModifierToolStack tool, int level, RestrictedCompoundTag tag) {
    Item item = tool.getItem();
    if (TinkerTags.Items.HELD.contains(item)) {
      tag.putBoolean(TOP_NBT_HAND, true);
    }
    if (TinkerTags.Items.HELMETS.contains(item)) {
      tag.putBoolean(TOP_NBT_HELMET, true);
    }
  }

  @Override
  public void beforeRemoved(IModifierToolStack tool, RestrictedCompoundTag tag) {
    tag.remove(TOP_NBT_HAND);
    tag.remove(TOP_NBT_HELMET);
  }
}
