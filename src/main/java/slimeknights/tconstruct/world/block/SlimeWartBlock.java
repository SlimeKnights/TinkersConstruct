package slimeknights.tconstruct.world.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Simple block to hide ichor */
public class SlimeWartBlock extends Block {
  private final FoliageType foliageType;
  public SlimeWartBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (foliageType != FoliageType.ICHOR) {
      super.fillItemCategory(group, items);
    }
  }
}
