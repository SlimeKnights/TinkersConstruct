package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import static slimeknights.tconstruct.tools.modules.TheOneProbeModule.TOP_NBT_HAND;
import static slimeknights.tconstruct.tools.modules.TheOneProbeModule.TOP_NBT_HELMET;

/** @deprecated use {@link slimeknights.tconstruct.tools.modules.TheOneProbeModule} */
@Deprecated
public class TOPModifier extends NoLevelsModifier {

  @Override
  public void addRawData(IToolStackView tool, int level, RestrictedCompoundTag tag) {
    if (tool.hasTag(TinkerTags.Items.HELD)) {
      tag.putBoolean(TOP_NBT_HAND, true);
    }
    if (tool.hasTag(TinkerTags.Items.HELMETS)) {
      tag.putBoolean(TOP_NBT_HELMET, true);
    }
  }

  @Override
  public void beforeRemoved(IToolStackView tool, RestrictedCompoundTag tag) {
    tag.remove(TOP_NBT_HAND);
    tag.remove(TOP_NBT_HELMET);
  }
}
