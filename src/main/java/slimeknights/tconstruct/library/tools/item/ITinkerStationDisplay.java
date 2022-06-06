package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface to implement for tools that also display in the tinker station
 */
public interface ITinkerStationDisplay extends ItemLike {
  /**
   * The "title" displayed in the GUI
   */
  default Component getLocalizedName() {
    return new TranslatableComponent(asItem().getDescriptionId());
  }

  /** @deprecated {@link #getStatInformation(IToolStackView, Player, List, TooltipKey, TooltipFlag)} */
  @Deprecated
  default List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, slimeknights.tconstruct.library.utils.TooltipKey key, TooltipFlag tooltipFlag) {
    tooltips = TooltipUtil.getDefaultStats(tool, player, tooltips, key.asMantle(), tooltipFlag);
    TooltipUtil.addAttributes(this, tool, player, tooltips, TooltipUtil.SHOW_MELEE_ATTRIBUTES, EquipmentSlot.MAINHAND);
    return tooltips;
  }

  /**
   * Returns the tool stat information for this tool
   * @param tool         Tool to display
   * @param tooltips     List of tooltips for display
   * @param tooltipFlag  Determines the type of tooltip to display
   */
  default List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
    return getStatInformation(tool, player, tooltips, slimeknights.tconstruct.library.utils.TooltipKey.fromMantle(key), tooltipFlag);
  }

  /**
   * Allows making attribute tooltips more efficient by not parsing the tool twice
   * @param tool   Tool to check for attributes
   * @param slot   Slot with attributes
   * @return  Attribute map
   */
  default Multimap<Attribute,AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
    return ImmutableMultimap.of();
  }
}
