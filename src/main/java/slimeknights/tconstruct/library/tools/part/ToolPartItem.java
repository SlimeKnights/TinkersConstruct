package slimeknights.tconstruct.library.tools.part;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeI18n;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.utils.SafeClientAccess;
import slimeknights.tconstruct.library.utils.TooltipKey;

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
    IMaterial material = this.getMaterial(stack);
    // add all traits to the info
    if (!this.checkMissingMaterialTooltip(stack, material, tooltip)) {
      for (ModifierEntry entry : MaterialRegistry.getInstance().getTraits(material.getIdentifier(), getStatType())) {
        tooltip.add(entry.getModifier().getDisplayName(entry.getLevel()));
      }
      // add stats
      if (Config.CLIENT.extraToolTips.get()) {
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (key == TooltipKey.SHIFT || key == TooltipKey.UNKNOWN) {
          this.addStatInfoTooltip(material, tooltip);
        } else {
          // info tooltip for detailed and component info
          tooltip.add(TextComponent.EMPTY);
          tooltip.add(TooltipUtil.TOOLTIP_HOLD_SHIFT);
        }
      }
      // mod handled by getCreatorModId
    }
  }

  /**
   * Adds the stat info for the given part to the tooltip
   * @param tooltip   Tooltip list
   * @param material  Material to add
   */
  protected void addStatInfoTooltip(IMaterial material, List<Component> tooltip) {
    MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId).ifPresent((stat) -> {
      List<Component> text = stat.getLocalizedInfo();
      if (!text.isEmpty()) {
        tooltip.add(TextComponent.EMPTY);
        tooltip.add(stat.getLocalizedName().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
        tooltip.addAll(stat.getLocalizedInfo());
      }
    });
  }

  /**
   * Adds the tooltip for missing materials
   * @param stack     Stack in case material is missing
   * @param material  Material to check
   * @param tooltip   Tooltip list
   * @return  True if the material is unknown
   */
  protected boolean checkMissingMaterialTooltip(ItemStack stack, IMaterial material, List<Component> tooltip) {
    if (material == IMaterial.UNKNOWN) {
      if (!TooltipUtil.isDisplay(stack)) {
        getMaterialId(stack).ifPresent(id -> tooltip.add(new TranslatableComponent(TConstruct.makeTranslationKey("tooltip", "part.missing_material"), id)));
      }
      return true;
    }
    else if (!canUseMaterial(material)) {
      TranslationHelper.addEachLine(ForgeI18n.parseMessage(MISSING_STATS_KEY, material.getTranslationKey(), materialStatId), tooltip);
    }

    return false;
  }
}
