package slimeknights.tconstruct.plugin.quark.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.plugin.quark.QuarkPlugin;
import slimeknights.tconstruct.shared.block.BlockSlime;
import vazkii.quark.api.INonSticky;

public class BlockSlimeQuark extends BlockSlime implements INonSticky {
  @Override
  public boolean canStickToBlock(World world, BlockPos pistonPos, BlockPos pos, BlockPos slimePos, IBlockState state, IBlockState slimeState, EnumFacing direction) {
    SlimeType type = state.getValue(TYPE);

    // pink connects to all
    if(type == SlimeType.PINK) {
      return true;
    }

    // only green connects to vanilla
    Block slimeBlock = slimeState.getBlock();
    if(slimeBlock == Blocks.SLIME_BLOCK) {
      return type == SlimeType.GREEN;
    }

    // for this, blocks only connect to same color or pink
    if(slimeBlock == this) {
      SlimeType slimeType = slimeState.getValue(TYPE);
      return type == slimeType || slimeType == SlimeType.PINK;
    }

    // green, blue, and blood act like the corresponding Quark slime blocks
    if(slimeBlock == QuarkPlugin.colorSlime) {
      int meta = slimeState.getBlock().getMetaFromState(slimeState);
      switch(type) {
        case GREEN: return meta == 2 || meta == 4; // cyan or yellow
        case BLUE: return meta == 1 || meta == 2 || meta == 3; // blue, cyan or magenta
        case BLOOD: return meta == 0 || meta == 3 || meta == 4; // red, magenta or yellow
      }
      return false; // purple and magma do not connect to any quark blocks
    }

    // stick to all else
    return true;
  }
}
