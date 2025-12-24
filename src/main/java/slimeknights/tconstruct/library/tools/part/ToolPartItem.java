package slimeknights.tconstruct.library.tools.part;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
  public static final String MATERIAL_KEY = TConstruct.makeTranslationKey("tooltip", "part.material_id");

  public final MaterialStatsId materialStatId;

  public ToolPartItem(Properties properties, MaterialStatsId id) {
    super(properties);

    this.materialStatId = id;
  }

  @Override
  public MaterialStatsId getStatType() {
    return this.materialStatId;
  }

  /** Helper to append the tooltip for this part. */
  public static void appendHoverText(IToolPart self, ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
    if (TooltipUtil.isDisplay(stack)) {
      return;
    }
    MaterialVariantId materialVariant = self.getMaterial(stack);
    MaterialId id = materialVariant.getId();
    if (!materialVariant.equals(IMaterial.UNKNOWN_ID)) {
      // internal material ID
      if (flag.isAdvanced()) {
        tooltip.add((Component.translatable(MATERIAL_KEY, materialVariant.toString())).withStyle(ChatFormatting.DARK_GRAY));
      }
      if (self.canUseMaterial(id)) {
        // add all valid traits
        TooltipKey key = SafeClientAccess.getTooltipKey();
        for (ModifierEntry entry : MaterialRegistry.getInstance().getTraits(id, self.getStatType())) {
          if (!entry.isBound()) {
            continue;
          }
          Component name = entry.getDisplayName();
          if (flag.isAdvanced() && Config.CLIENT.modifiersIDsInAdvancedTooltips.get()) {
            tooltip.add(Component.translatable(TooltipUtil.KEY_ID_FORMAT, name, Component.literal(entry.getModifier().getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
          } else {
            tooltip.add(name);
          }
          // holding control shows descriptions for traits
          if (key == TooltipKey.CONTROL) {
            // skip the flavor line, trait name will take its spot instead
            List<Component> description = entry.getModifier().getDescriptionList(entry.getLevel());
            for (int i = 1; i < description.size(); i++) {
              tooltip.add(description.get(i).plainCopy().withStyle(ChatFormatting.GRAY));
            }
          }
        }
        // add stats on shift
        if (key == TooltipKey.SHIFT || key == TooltipKey.UNKNOWN) {
          MaterialRegistry.getInstance().getMaterialStats(id, self.getStatType()).ifPresent(stat -> {
            List<Component> text = stat.getLocalizedInfo();
            if (!text.isEmpty()) {
              tooltip.add(Component.empty());
              tooltip.add(stat.getLocalizedName().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
              tooltip.addAll(stat.getLocalizedInfo());
            }
          });
        } else if (key != TooltipKey.CONTROL) {
          // info tooltip for detailed and component info
          tooltip.add(Component.empty());
          tooltip.add(TooltipUtil.TOOLTIP_HOLD_SHIFT);
          tooltip.add(TooltipUtil.TOOLTIP_HOLD_CTRL);
        }
      } else {
        // is the material missing, or is it not valid for this stat type?
        IMaterial material = MaterialRegistry.getMaterial(id);
        if (material == IMaterial.UNKNOWN) {
          tooltip.add(Component.translatable(MISSING_MATERIAL_KEY, id));
        } else {
          tooltip.add(Component.translatable(MISSING_STATS_KEY, self.getStatType()).withStyle(ChatFormatting.GRAY));
        }
      }
    }
    // mod handled by getCreatorModId
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
    appendHoverText(this, stack, tooltip, flag);
  }

  /**
   * Adds the stat info for the given part to the tooltip
   * @param tooltip   Tooltip list
   * @param material  Material to add
   */
  @Deprecated(forRemoval = true)
  protected void addStatInfoTooltip(MaterialId material, List<Component> tooltip) {
    MaterialRegistry.getInstance().getMaterialStats(material, this.materialStatId).ifPresent((stat) -> {
      List<Component> text = stat.getLocalizedInfo();
      if (!text.isEmpty()) {
        tooltip.add(Component.empty());
        tooltip.add(stat.getLocalizedName().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
        tooltip.addAll(stat.getLocalizedInfo());
      }
    });
  }
}
