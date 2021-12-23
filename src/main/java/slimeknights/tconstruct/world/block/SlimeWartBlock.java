package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.shared.block.SlimeType;

/** Simple block to hide ichor */
public class SlimeWartBlock extends Block {
  private final SlimeType foliageType;
  public SlimeWartBlock(Properties properties, SlimeType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (foliageType != SlimeType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }
}
