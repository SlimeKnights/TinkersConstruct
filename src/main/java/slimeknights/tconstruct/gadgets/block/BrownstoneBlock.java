package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrownstoneBlock extends Block {

  public BrownstoneBlock() {
    super(AbstractBlock.Settings.of(Material.STONE).strength(3.0F, 20.0F).sounds(BlockSoundGroup.STONE));
  }

  @Override
  public void onSteppedOn(World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn.isTouchingWater()) {
      entityIn.setVelocity(entityIn.getVelocity().multiply(1.20D, 1.0D, 1.20D));
    } else {
      entityIn.setVelocity(entityIn.getVelocity().multiply(1.25D, 1.0D, 1.25D));
    }
  }
}
