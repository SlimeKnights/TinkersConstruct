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

public class ExcavatorTool extends ShovelTool {

  public static final float DURABILITY_MODIFIER = 1.75f;

  public ExcavatorTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return AoeToolInteractionUtil.calculateAOEBlocks(stack, world, player, origin, 3, 3, 1);
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
