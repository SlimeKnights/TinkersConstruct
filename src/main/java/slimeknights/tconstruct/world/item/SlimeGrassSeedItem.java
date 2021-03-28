package slimeknights.tconstruct.world.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nullable;

public class SlimeGrassSeedItem extends TooltipItem {
  private final FoliageType foliage;
  public SlimeGrassSeedItem(Properties properties, FoliageType foliage) {
    super(properties);
    this.foliage = foliage;
  }

  /** Gets the slime type for the given block */
  @Nullable
  private SlimeType getSlimeType(Block block) {
    for (SlimeType type : SlimeType.values()) {
      if (TinkerWorld.allDirt.get(type) == block) {
        return type;
      }
    }
    return null;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    BlockPos pos = context.getPos();
    World world = context.getWorld();
    BlockState state = world.getBlockState(pos);
    SlimeType type = getSlimeType(state.getBlock());
    if (type != null) {
      if (!world.isRemote) {
        BlockState grassState = TinkerWorld.slimeGrass.get(type).get(foliage).getDefaultState();
        world.setBlockState(pos, grassState);
        world.playSound(null, pos, grassState.getSoundType(world, pos, context.getPlayer()).getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isCreative()) {
          context.getItem().shrink(1);
        }
      }
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliage != FoliageType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }
}
