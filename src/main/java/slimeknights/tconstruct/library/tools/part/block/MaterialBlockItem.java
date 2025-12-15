package slimeknights.tconstruct.library.tools.part.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.MaterialItem;

import javax.annotation.Nullable;
import java.util.List;

/** Implementation of {@link MaterialItem} on a {@link BlockItem}. */
public class MaterialBlockItem extends BlockItem implements IMaterialItem {
  public MaterialBlockItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public MaterialVariantId getMaterial(ItemStack stack) {
    return MaterialItem.getMaterialId(stack.getTag());
  }

  @Override
  public Component getName(ItemStack stack) {
    return MaterialItem.getName(this, stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    MaterialItem.appendHoverText(this, stack, tooltip, flag);
    super.appendHoverText(stack, level, tooltip, flag);
  }

  @Nullable
  @Override
  public String getCreatorModId(ItemStack stack) {
    return MaterialItem.getCreatorModId(this, stack);
  }

  @Override
  public void verifyTagAfterLoad(CompoundTag tag) {
    MaterialItem.verifyTag(tag);
  }
}
