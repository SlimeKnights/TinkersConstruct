package slimeknights.tconstruct.world.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;

import javax.annotation.Nullable;

import net.minecraft.item.Item.Properties;

public class SlimeGrassSeedItem extends TooltipItem {
  private final SlimeType foliage;
  public SlimeGrassSeedItem(Properties properties, SlimeType foliage) {
    super(properties);
    this.foliage = foliage;
  }

  /** Gets the slime type for the given block */
  @Nullable
  private static SlimeType getSlimeType(Block block) {
    for (SlimeType type : SlimeType.values()) {
      if (TinkerWorld.allDirt.get(type) == block) {
        return type;
      }
    }
    return null;
  }

  /** Gets the vines associated with these seeds */
  @Nullable
  private Block getVines() {
    switch (foliage) {
      case SKY:   return TinkerWorld.skySlimeVine.get();
      case ENDER: return TinkerWorld.enderSlimeVine.get();
    }
    return null;
  }

  @Override
  public ActionResultType useOn(ItemUseContext context) {
    BlockPos pos = context.getClickedPos();
    World world = context.getLevel();
    BlockState state = world.getBlockState(pos);
    BlockState newState = null;

    // try vines first
    if (state.getBlock() == Blocks.VINE) {
      Block slimyVines = getVines();
      if (slimyVines != null) {
        // copy over the directions
        newState = slimyVines.defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.START);
        for (BooleanProperty prop : VineBlock.PROPERTY_BY_DIRECTION.values()) {
          if (state.getValue(prop)) {
            newState = newState.setValue(prop, true);
          }
        }
      }
    }

    // if vines did not succeed, try grass
    if (newState == null) {
      SlimeType type = getSlimeType(state.getBlock());
      if (type != null) {
        newState = TinkerWorld.slimeGrass.get(type).get(foliage).defaultBlockState();
      } else {
        return ActionResultType.PASS;
      }
    }

    // will have a state at this point
    if (!world.isClientSide) {
      world.setBlockAndUpdate(pos, newState);
      world.playSound(null, pos, newState.getSoundType(world, pos, context.getPlayer()).getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
      PlayerEntity player = context.getPlayer();
      if (player == null || !player.isCreative()) {
        context.getItemInHand().shrink(1);
      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliage != SlimeType.ICHOR) {
      super.fillItemCategory(group, items);
    }
  }
}
