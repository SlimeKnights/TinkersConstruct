package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.tools.IToolPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ToolPart extends MaterialItem implements IToolPart {

  public final MaterialStatsId materialStatId;

  public ToolPart(Properties properties, MaterialStatsId id) {
    super(properties);

    this.materialStatId = id;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      if (MaterialRegistry.initialized()) {
        for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
          if (this.canUseMaterial(material)) {
            items.add(this.getItemstackWithMaterial(material));
            if (!Config.COMMON.listAllPartMaterials.get()) {
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public boolean canUseMaterial(IMaterial material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId).isPresent();
  }

  @Override
  public boolean hasUseForStat(MaterialStatsId stat) {
    //TODO check if part is correct for any tool's required components
    return true;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    IMaterial material = this.getMaterial(stack);

    // Material traits/info
    boolean shift = Util.isShiftKeyDown();

    // Stats
    if (Config.COMMON.chestsKeepInventory.get()) {
      if (!shift) {
        // info tooltip for detailed and component info
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new TranslationTextComponent("tooltip.tool.hold_shift"));
      } else {
        tooltip.addAll(this.getTooltipStatsInfo(material));
      }
    }

    //tooltip.addAll(getAddedByInfo(material));
  }

  public List<ITextComponent> getTooltipStatsInfo(IMaterial material) {
    ImmutableList.Builder<ITextComponent> builder = ImmutableList.builder();

    Optional<IMaterialStats> materialStat = MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId);
    if (materialStat.isPresent()) {
      IMaterialStats stat = materialStat.get();

      if (this.hasUseForStat(stat.getIdentifier())) {
        List<ITextComponent> text = stat.getLocalizedInfo();
        if (!text.isEmpty()) {
          builder.add(new StringTextComponent(""));
          builder.add(new StringTextComponent(TextFormatting.WHITE.toString() + TextFormatting.UNDERLINE).appendSibling(new TranslationTextComponent(stat.getLocalizedName())));
          builder.addAll(stat.getLocalizedInfo());
        }
      }
    }

    return builder.build();
  }

  public boolean checkMissingMaterialTooltip(ItemStack stack, List<String> tooltip) {
    return checkMissingMaterialTooltip(stack, tooltip, "");
  }

  public boolean checkMissingMaterialTooltip(ItemStack stack, List<String> tooltip, String statIdentifier) {
    IMaterial material = this.getMaterial(stack);

    if (material == IMaterial.UNKNOWN) {
      CompoundNBT tagSafe = TagUtil.getTagSafe(stack);
      String materialId = tagSafe.getString(Tags.PART_MATERIAL);
      ITextComponent error;

      if (!materialId.isEmpty()) {
        error = new TranslationTextComponent("tooltip.part.missinmg_materal", materialId);
      } else {
        error = new TranslationTextComponent("tooltip.part.missing_info");
      }
      tooltip.add(error.getFormattedText());
      return true;
    }
    /*else if(statIdentifier != null && material.getStats(statIdentifier) == null) {
      //tooltip.addAll(LocUtils.getTooltips(Util.translateFormatted("tooltip.part.missing_stats", material.getLocalizedName(), statIdentifier)));
    }*/

    return false;
  }

  @Nonnull
  @OnlyIn(Dist.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return TinkerClient.fontRenderer;
  }
}
