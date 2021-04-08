package slimeknights.tconstruct.world.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nullable;

public class SlimeGrassSeedItem extends TooltipItem {
  private final FoliageType foliage;
  public SlimeGrassSeedItem(Settings properties, FoliageType foliage) {
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
  public ActionResult useOnBlock(ItemUsageContext context) {
    BlockPos pos = context.getBlockPos();
    World world = context.getWorld();
    BlockState state = world.getBlockState(pos);
    SlimeType type = getSlimeType(state.getBlock());
    if (type != null) {
      if (!world.isClient) {
        BlockState grassState = TinkerWorld.slimeGrass.get(type).get(foliage).getDefaultState();
        world.setBlockState(pos, grassState);
        world.playSound(null, pos, grassState.getSoundType(world, pos, context.getPlayer()).getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isCreative()) {
          context.getStack().decrement(1);
        }
      }
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }

  @Override
  public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.foliage != FoliageType.ICHOR) {
      super.appendStacks(group, items);
    }
  }
}
