package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
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

  /**
   * Returns the tool stat information for this tool
   * @param tool         Tool to display
   * @param tooltips     List of tooltips for display
   * @param tooltipFlag  Determines the type of tooltip to display
   */
  default List<Component> getStatInformation(IModifierToolStack tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
    tooltips = TooltipUtil.getDefaultStats(tool, player, tooltips, key, tooltipFlag);
    TooltipUtil.addAttributes(this, tool, player, tooltips, TooltipUtil.SHOW_MELEE_ATTRIBUTES, EquipmentSlot.MAINHAND);
    return tooltips;
  }

  /**
   * Allows making attribute tooltips more efficient by not parsing the tool twice
   * @param tool   Tool to check for attributes
   * @param slot   Slot with attributes
   * @return  Attribute map
   */
  default Multimap<Attribute,AttributeModifier> getAttributeModifiers(IModifierToolStack tool, EquipmentSlot slot) {
    return ImmutableMultimap.of();
  }

  /**
   * Combines the given display name with the material names to form the new given name
   *
   * @param itemName the standard display name
   * @param materials the list of materials
   * @return the combined item name
   */
  static Component getCombinedItemName(ItemStack stack, Component itemName, Collection<IMaterial> materials) {
    if (materials.isEmpty() || materials.stream().allMatch(IMaterial.UNKNOWN::equals)) {
      return itemName;
    }

    if (materials.size() == 1) {
      IMaterial material = materials.iterator().next();
      // direct name override for this tool
      if (!stack.isEmpty()) {
        MaterialId id = material.getIdentifier();
        String key = stack.getDescriptionId() + ".material." + id.getNamespace() + "." + id.getPath();
        if (Util.canTranslate(key)) {
          return new TranslatableComponent(key);
        }
      }
      // name format override
      if (Util.canTranslate(material.getTranslationKey() + ".format")) {
        return new TranslatableComponent(material.getTranslationKey() + ".format", itemName);
      }

      return new TranslatableComponent(materials.iterator().next().getTranslationKey()).append(new TextComponent(" ")).append(itemName);
    }

    // multiple materials. we'll have to combine
    TextComponent name = new TextComponent("");

    Iterator<IMaterial> iter = materials.iterator();

    IMaterial material = iter.next();
    name.append(new TranslatableComponent(material.getTranslationKey()));

    while (iter.hasNext()) {
      material = iter.next();
      name.append("-").append(new TranslatableComponent(material.getTranslationKey()));
    }

    name.append(" ").append(itemName);

    return name;
  }
}
