package slimeknights.tconstruct.tables.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.library.tools.part.block.MaterialBlockItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Material block item for the anvils, which match a tool part for their valid materials.
 * Used for the sake of {@link slimeknights.tconstruct.tools.TinkerToolParts#fakeStorageBlock} crafting anvils.
 */
public class AnvilBlockItem extends MaterialBlockItem {
  private final Supplier<? extends IMaterialItem> matching;
  public AnvilBlockItem(Block block, Properties properties, Supplier<? extends IMaterialItem> matching) {
    super(block, properties);
    this.matching = matching;
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    return matching.get().canUseMaterial(material);
  }


  /* Tooltip */

  @Override
  public Component getName(ItemStack stack) {
    // don't put tool material in name
    return Component.translatable(this.getDescriptionId(stack));
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    // ditch the super call advanced tooltip material ID, we will handle it ourselves later
    this.getBlock().appendHoverText(stack, level, tooltip, flag);
    MaterialVariantId material = getMaterial(stack);
    if (!IMaterial.UNKNOWN_ID.equals(material)) {
      // put tool material in tooltip. Its technically below texture but the two should never coexist.
      tooltip.add(this.matching.get().asItem().getName(stack).copy().withStyle(ChatFormatting.GRAY));
      // add ID if advanced
      if (flag.isAdvanced()) {
        tooltip.add(Component.translatable(ToolPartItem.MATERIAL_KEY, material).withStyle(ChatFormatting.DARK_GRAY));
      }
    }
  }

  @Nullable
  @Override
  public String getCreatorModId(ItemStack stack) {
    // reset to default, just show the anvil ID. Material variants mean less here
    return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
  }

  @Override
  public void addVariants(Consumer<ItemStack> items, String showOnlyMaterial) {
    RetexturedHelper.addTagVariants(stack -> {
      items.accept(stack);
      return false;
    }, this, TinkerTags.Items.ANVIL_METAL);
    super.addVariants(items, showOnlyMaterial);
  }
}
