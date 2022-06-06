package slimeknights.tconstruct.library.tools.part;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extension of {@link MaterialItem} which adds stats to the tooltip and has a set stat type
 */
public class ToolPartItem extends MaterialItem implements IToolPart {
  private static final Component MISSING_INFO = TConstruct.makeTranslation("tooltip", "part.missing_info");
  private static final String MISSING_MATERIAL_KEY = TConstruct.makeTranslationKey("tooltip", "part.missing_material");
  private static final String MISSING_STATS_KEY = TConstruct.makeTranslationKey("tooltip", "part.missing_stats");

  public final MaterialStatsId materialStatId;

  public ToolPartItem(Properties properties, MaterialStatsId id) {
    super(properties);

    this.materialStatId = id;
  }

  @Override
  public MaterialStatsId getStatType() {
    return this.materialStatId;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    if (TooltipUtil.isDisplay(stack)) {
      return;
    }

    // add all traits to the info
    MaterialVariantId materialVariant = this.getMaterial(stack);
    MaterialId id = materialVariant.getId();
    if (!materialVariant.equals(IMaterial.UNKNOWN_ID)) {
      if (canUseMaterial(id)) {
        for (ModifierEntry entry : MaterialRegistry.getInstance().getTraits(id, getStatType())) {
          tooltip.add(entry.getModifier().getDisplayName(entry.getLevel()));
        }
        // add stats
        if (Config.CLIENT.extraToolTips.get()) {
          TooltipKey key = SafeClientAccess.getTooltipKey();
          if (key == TooltipKey.SHIFT || key == TooltipKey.UNKNOWN) {
            this.addStatInfoTooltip(id, tooltip);
          } else {
            // info tooltip for detailed and component info
            tooltip.add(TextComponent.EMPTY);
            tooltip.add(TooltipUtil.TOOLTIP_HOLD_SHIFT);
          }
        }
      } else {
        // is the material missing, or is it not valid for this stat type?
        IMaterial material = MaterialRegistry.getMaterial(id);
        if (material == IMaterial.UNKNOWN) {
          tooltip.add(new TranslatableComponent(MISSING_MATERIAL_KEY, id));
        } else {
          tooltip.add(new TranslatableComponent(MISSING_STATS_KEY, materialStatId).withStyle(ChatFormatting.GRAY));
        }
      }
    }
    // mod handled by getCreatorModId
  }

  /**
   * Adds the stat info for the given part to the tooltip
   * @param tooltip   Tooltip list
   * @param material  Material to add
   */
  protected void addStatInfoTooltip(MaterialId material, List<Component> tooltip) {
    MaterialRegistry.getInstance().getMaterialStats(material, this.materialStatId).ifPresent((stat) -> {
      List<Component> text = stat.getLocalizedInfo();
      if (!text.isEmpty()) {
        tooltip.add(TextComponent.EMPTY);
        tooltip.add(stat.getLocalizedName().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
        tooltip.addAll(stat.getLocalizedInfo());
      }
    });
  }
}
