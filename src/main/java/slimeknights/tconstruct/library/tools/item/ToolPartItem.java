package slimeknights.tconstruct.library.tools.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Extension of {@link MaterialItem} which adds stats to the tooltip and has a set stat type
 */
public class ToolPartItem extends MaterialItem implements IToolPart {
  private static final ITextComponent MISSING_INFO = Util.makeTranslation("item", "part.missing_info");
  private static final String MISSING_MATERIAL_KEY = Util.makeTranslationKey("item", "part.missing_material");
  private static final String MISSING_STATS_KEY = Util.makeTranslationKey("item", "part.missing_stats");

  public final MaterialStatsId materialStatId;

  public ToolPartItem(Properties properties, MaterialStatsId id) {
    super(properties);

    this.materialStatId = id;
  }

  @Override
  public boolean canUseMaterial(IMaterial material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId).isPresent();
  }

  @Override
  public MaterialStatsId getStatType() {
    return this.materialStatId;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    IMaterial material = this.getMaterial(stack);
    // add all traits to the info
    if (!this.checkMissingMaterialTooltip(stack, material, tooltip)) {
      for (ModifierEntry entry : MaterialRegistry.getInstance().getTraits(material.getIdentifier(), getStatType())) {
        tooltip.add(entry.getModifier().getDisplayName(entry.getLevel()));
      }
      // add stats
      if (Config.CLIENT.extraToolTips.get()) {
        if (Screen.hasShiftDown()) {
          this.addStatInfoTooltip(material, tooltip);
        } else {
          // info tooltip for detailed and component info
          tooltip.add(StringTextComponent.EMPTY);
          tooltip.add(ToolCore.TOOLTIP_HOLD_SHIFT);
        }
      }
      // and finally, mod
      addModTooltip(material, tooltip);
    }
  }

  /**
   * Adds the stat info for the given part to the tooltip
   * @param tooltip   Tooltip list
   * @param material  Material to add
   */
  protected void addStatInfoTooltip(IMaterial material, List<ITextComponent> tooltip) {
    MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId).ifPresent((stat) -> {
      List<ITextComponent> text = stat.getLocalizedInfo();
      if (!text.isEmpty()) {
        tooltip.add(new StringTextComponent(""));
        tooltip.add(stat.getLocalizedName().mergeStyle(TextFormatting.WHITE, TextFormatting.UNDERLINE));
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
  protected boolean checkMissingMaterialTooltip(ItemStack stack, IMaterial material, List<ITextComponent> tooltip) {
    if (material == IMaterial.UNKNOWN) {
      Optional<MaterialId> materialId = getMaterialId(stack);
      materialId.ifPresent(id -> tooltip.add(new TranslationTextComponent(MISSING_MATERIAL_KEY, id)));
      return true;
    }
    else if (!canUseMaterial(material)) {
      TranslationHelper.addEachLine(ForgeI18n.parseMessage(MISSING_STATS_KEY, material.getTranslationKey(), materialStatId), tooltip);
    }

    return false;
  }
}
