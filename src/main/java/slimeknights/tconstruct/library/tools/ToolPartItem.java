package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
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
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

import javax.annotation.Nullable;
import java.util.List;

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
  public MaterialStatsId getStatType() {
    return this.materialStatId;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    IMaterial material = this.getMaterial(stack);

    // Material traits/info
    boolean shift = Util.isShiftKeyDown();

    if (!this.checkMissingMaterialTooltip(stack, tooltip)) {
      ModifierEntry entry = material.getTrait();
      if (entry != null) {
        tooltip.add(entry.getModifier().getDisplayName(entry.getLevel()));
      }
    }

    // Stats
    if (Config.CLIENT.extraToolTips.get()) {
      if (!shift) {
        // info tooltip for detailed and component info
        tooltip.add(StringTextComponent.EMPTY);
        tooltip.add(ToolCore.TOOLTIP_HOLD_SHIFT);
      }
      else {
        tooltip.addAll(this.getTooltipStatsInfo(material));
      }
    }

    tooltip.addAll(this.getAddedByInfo(material));
  }

  public List<ITextComponent> getTooltipStatsInfo(IMaterial material) {
    ImmutableList.Builder<ITextComponent> builder = ImmutableList.builder();

    MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), this.materialStatId).ifPresent((stat) -> {
      List<ITextComponent> text = stat.getLocalizedInfo();
      if (!text.isEmpty()) {
        builder.add(new StringTextComponent(""));
        builder.add(stat.getLocalizedName().mergeStyle(TextFormatting.WHITE, TextFormatting.UNDERLINE));
        builder.addAll(stat.getLocalizedInfo());
      }
    });

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
    else if(!MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), materialStatId).isPresent()) {
      TranslationHelper.addEachLine(ForgeI18n.parseMessage("tooltip.part.missing_stats", material.getTranslationKey(), materialStatId), tooltip);
    }

    return false;
  }
}
