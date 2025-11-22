package slimeknights.tconstruct.library.tools.definition.module.display;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Make the tool display name by displaying all materials that match some criteria. */
public interface MaterialToolName extends ToolNameHook {
  /** Translation key for the tool name format string */
  Component MATERIAL_SEPARATOR = TConstruct.makeTranslation("item", "tool.material_separator");

  /** Checks if the given material should show in the tooltip */
  boolean shouldDisplayMaterial(int index, MaterialStatsId statType, MaterialVariantId material);

  @Override
  default Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool) {
    Component itemName = Component.translatable(stack.getItem().getDescriptionId());
    List<MaterialStatsId> components = ToolMaterialHook.stats(definition);
    // if no materials, direct name
    if (components.isEmpty()) {
      return itemName;
    }
    // if not enough materials, also skip processing
    MaterialNBT materials = ToolNameHook.getTool(stack, tool).getMaterials();
    if (materials.isEmpty()) {
      return itemName;
    }
    // first, find a set of unique materials to display
    int size = Math.min(components.size(), materials.size());
    Set<MaterialVariantId> displayMaterials = new LinkedHashSet<>(size);
    for (int i = 0; i < size; i++) {
      MaterialVariantId material = materials.get(i).getVariant();
      if (!IMaterial.UNKNOWN_ID.equals(material) && shouldDisplayMaterial(i, components.get(i), material)) {
        displayMaterials.add(material);
      }
    }
    // nothing? give up
    if (displayMaterials.isEmpty()) {
      return itemName;
    }
    // find the name of the materials
    Component materialName;
    if (displayMaterials.size() == 1) {
      // for one material, display outright
      return nameForMaterial(displayMaterials.iterator().next(), itemName);
    } else {
      // mix of materials get hyphenated
      Set<Component> names = new LinkedHashSet<>(displayMaterials.size());
      for (MaterialVariantId id : displayMaterials) {
        names.add(MaterialTooltipCache.getDisplayName(id));
      }
      MutableComponent builder = Component.literal("");
      Iterator<Component> iter = names.iterator();
      builder.append(iter.next());
      while (iter.hasNext()) {
        builder.append(MATERIAL_SEPARATOR).append(iter.next());
      }
      // build final name
      return Component.translatable(TooltipUtil.KEY_FORMAT, builder, itemName);
    }
  }

  /** Gets the tool name for the given material */
  static Component nameForMaterial(MaterialVariantId material, Component itemName) {
    // use material format if requested
    String format = MaterialTooltipCache.getKey(material) + ".format";
    if (Util.canTranslate(format)) {
      return Component.translatable(format, itemName);
    }
    // fallback to standard format with default material name
    return Component.translatable(TooltipUtil.KEY_FORMAT, MaterialTooltipCache.getDisplayName(material), itemName);
  }
}
