package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.CommonsClientEvents;
import slimeknights.tconstruct.tools.IToolPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ToolPartItem extends MaterialItem implements IToolPart {

  public final MaterialStatsId materialStatId;

  public ToolPartItem(Properties properties, MaterialStatsId id) {
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
    return this.materialStatId.equals(stat);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    IMaterial material = this.getMaterial(stack);

    // Material traits/info
    boolean shift = Util.isShiftKeyDown();

    // todo traits
    if(!this.checkMissingMaterialTooltip(stack, tooltip)) {
      tooltip.addAll(this.getTooltipTraitInfo(material));
    }

    // Stats
    if (Config.CLIENT.extraToolTips.get()) {
      if (!shift) {
        // info tooltip for detailed and component info
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new TranslationTextComponent("tooltip.tool.hold_shift"));
      }
      else {
        tooltip.addAll(this.getTooltipStatsInfo(material));
      }
    }

    tooltip.addAll(this.getAddedByInfo(material));
  }

  private List<? extends ITextComponent> getTooltipTraitInfo(IMaterial material) {
    //TODO IMPLEMENT

    List<ITextComponent> tooltips = Lists.newLinkedList();

    tooltips.add(new StringTextComponent("please implement getTooltipTraitInfo"));

    return tooltips;
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
          builder.add(stat.getLocalizedName().applyTextStyles(TextFormatting.WHITE, TextFormatting.UNDERLINE));
          builder.addAll(stat.getLocalizedInfo());
        }
      }
    }

    return builder.build();
  }

  public List<ITextComponent> getAddedByInfo(IMaterial material) {
    ImmutableList.Builder<ITextComponent> builder = ImmutableList.builder();

    if (MaterialRegistry.getInstance().getMaterial(material.getIdentifier()) != IMaterial.UNKNOWN) {
      builder.add(new StringTextComponent(""));
      for (ModInfo modInfo : ModList.get().getMods()) {
        if (modInfo.getModId().equalsIgnoreCase(material.getIdentifier().getNamespace())) {
          builder.add(new TranslationTextComponent("tooltip.part.material_added_by", modInfo.getDisplayName()));
        }
      }
    }

    return builder.build();
  }

  public boolean checkMissingMaterialTooltip(ItemStack stack, List<ITextComponent> tooltip) {
    return checkMissingMaterialTooltip(stack, tooltip, null);
  }

  public boolean checkMissingMaterialTooltip(ItemStack stack, List<ITextComponent> tooltip, MaterialStatsId statIdentifier) {
    IMaterial material = this.getMaterial(stack);

    if (material == IMaterial.UNKNOWN) {
      CompoundNBT tagSafe = TagUtil.getTagSafe(stack);
      String materialId = tagSafe.getString(Tags.PART_MATERIAL);
      if (!materialId.isEmpty()) {
        tooltip.add(new TranslationTextComponent("tooltip.part.missing_material", materialId));
      }
      else {
        tooltip.add(new TranslationTextComponent("tooltip.part.missing_info"));
      }
      return true;
    }
    else if(!MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), statIdentifier).isPresent()) {
      tooltip.addAll(LocUtils.getTooltips(Util.translateFormatted("tooltip.part.missing_stats", material.getTranslationKey(), statIdentifier)));
    }

    return false;
  }

  @Nonnull
  @OnlyIn(Dist.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return CommonsClientEvents.fontRenderer;
  }
}
