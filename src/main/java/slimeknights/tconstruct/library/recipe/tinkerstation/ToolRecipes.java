package slimeknights.tconstruct.library.recipe.tinkerstation;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.tools.definition.module.ToolHooks.TOOL_PARTS;
import static slimeknights.tconstruct.tools.TinkerTools.plateArmor;

public class ToolRecipes {

  @Getter
  private final ToolDefinition definition;

  private final StationSlotLayout slotLayout;

  public ToolRecipes(ToolDefinition definition) {
    this.definition = definition;
    ResourceLocation id = switch(definition.getId().toString()) {
      case "tconstruct:plate_helmet":
      case "tconstruct:plate_chestplate":
      case "tconstruct:plate_leggings":
      case "tconstruct:plate_boots":
        yield new ResourceLocation("tconstruct", "plate_armor");
      default:
        yield definition.getId();
    };
    this.slotLayout = StationSlotLayoutLoader.getInstance().get(id);
  }

  static List<Material> MATERIALS = MaterialRegistry.getMaterials()
    .stream()
    .map(material -> (Material) material)
    .filter(material -> !material.isHidden())
    .toList();

  public List<LayoutSlot> getSlots() {
    return slotLayout.getInputSlots();
  }

  public ItemStack getOutputTool() {
    return switch(definition.getId().toString()) {
      case "tconstruct:plate_helmet" -> plateArmor.get(ArmorSlotType.HELMET).getRenderTool();
      case "tconstruct:plate_chestplate" -> plateArmor.get(ArmorSlotType.CHESTPLATE).getRenderTool();
      case "tconstruct:plate_leggings" -> plateArmor.get(ArmorSlotType.LEGGINGS).getRenderTool();
      case "tconstruct:plate_boots" -> plateArmor.get(ArmorSlotType.BOOTS).getRenderTool();
      default -> StationSlotLayoutLoader.getInstance().get(definition.getId()).getIcon().getValue(ItemStack.class);
    };
  }

  public boolean isBroadTool() {
    // assumption might not always be true
    return 8 < slotLayout.getSortIndex() && slotLayout.getSortIndex() < 15;
  }

  // a 2d list of each part and then each variant of that part
  public List<List<ItemStack>> getInputsParts() {
    return definition
      .getHook(TOOL_PARTS)
      .getParts(definition)
      .stream()
      .map(part -> MATERIALS.stream()
        .filter(part::canUseMaterial)
        .map(material -> part.withMaterial(material.getIdentifier()))
        .toList()
      ).toList();
  }

  // use display parts to be more consistent
  public List<ItemStack> getDisplayParts() {
    List<IToolPart> parts = definition
      .getHook(TOOL_PARTS)
      .getParts(definition)
      .stream()
      .toList();

    List<ItemStack> items = new ArrayList<>();
    for (int i = 0; i < parts.size(); i++) {
      ItemStack item = parts.get(i).withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i));
      item.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
      items.add(item);
    }
    return items;
  }

}
