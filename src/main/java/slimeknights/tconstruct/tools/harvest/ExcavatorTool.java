package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import javax.annotation.Nonnull;
import java.util.List;

public class ExcavatorTool extends ShovelTool implements IAoeTool {

  public static final float DURABILITY_MODIFIER = 1.75f;

  public ExcavatorTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    ActionResultType resultType = super.onItemUse(context);
    PlayerEntity playerEntity = context.getPlayer();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    ItemStack itemStack = playerEntity.getHeldItem(context.getHand());

    Block block = world.getBlockState(pos).getBlock();
    if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
      for (BlockPos aoePos : this.getAOEBlocks(itemStack, world, playerEntity, pos)) {
        // stop if the tool breaks during the process
        if (ToolData.from(itemStack).getStats().broken) {
          break;
        }

        // TODO Add in 1.16 using methods

        /*ItemUseContext context1 = new ItemUseContext(context.getPlayer(), context.getHand(), null) {
          public BlockPos getPos() {
            return aoePos;
          }

          public Direction getFace() {
            return context.getFace();
          }

          public Vec3d getHitVec() {
            return context.getHitVec();
          }

          public boolean isInside() {
            return context.isInside();
          }
        };

        ActionResultType aoeResult = Items.DIAMOND_SHOVEL.onItemUse(context1);
        // if we pass on an earlier block, check if another block succeeds here instead
        if (resultType != ActionResultType.SUCCESS) {
          resultType = aoeResult;
        }

        if (aoeResult == ActionResultType.SUCCESS) {
          //TODO event
          //TinkerToolEvent.OnShovelMakePath.fireEvent(stack, player, world, aoePos);
        }*/
      }
    }

    return resultType;
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return AoeToolInteractionUtil.calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }

  @Override
  public boolean isAoeHarvestTool() {
    return true;
  }

  /*@Override
  public int[] getRepairParts() {
    return new int[] { 1, 2 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }*/

  @Override
  public StatsNBT buildToolStats(List<IMaterial> materials) {
    StatsNBT statsNBT = super.buildToolStats(materials);

    return new StatsNBT((int) (statsNBT.durability * DURABILITY_MODIFIER), statsNBT.harvestLevel, statsNBT.attack,
      statsNBT.miningSpeed, statsNBT.attackSpeedMultiplier, statsNBT.freeModifiers, statsNBT.broken);
  }
}
