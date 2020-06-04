package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public class BlueSlimeEntity extends SlimeEntity {

  public BlueSlimeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  public static boolean canSpawnHere(EntityType<BlueSlimeEntity> entityType, IWorld worldIn, SpawnReason spawnReason, BlockPos pos, Random random) {
    IFluidState ifluidstate = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (ifluidstate.isTagged(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(TinkerTags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }

  @Override
  protected ResourceLocation getLootTable() {
    return this.getSlimeSize() == 1 ? this.getType().getLootTable() : LootTables.EMPTY;
  }

  @Override
  protected boolean spawnCustomParticles() {
    if (this.getEntityWorld().isRemote) {
      int i = this.getSlimeSize();
      for (int j = 0; j < i * 8; ++j) {
        float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
        float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
        float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
        float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
        double d0 = this.getPosX() + (double) f2;
        double d1 = this.getPosZ() + (double) f3;
        double d2 = this.getBoundingBox().minY;
        TConstruct.proxy.spawnSlimeParticle(this.getEntityWorld(), d0, d2, d1);
      }
    }
    return true;
  }
}
