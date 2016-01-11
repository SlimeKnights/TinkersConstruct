package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class SlimeLakeGenerator implements IWorldGenerator {

  private final IBlockState liquid;
  private final IBlockState lakeBottomBlock;
  private final IBlockState[] slimeBlocks;

  public SlimeLakeGenerator(IBlockState liquid, IBlockState lakeBottomBlock, IBlockState... slimeBlocks) {
    this.liquid = liquid;
    this.lakeBottomBlock = lakeBottomBlock;
    this.slimeBlocks = slimeBlocks;
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    generateLake(random, world, world.getHeight(new BlockPos(chunkX*16, 0, chunkZ*16)));
  }

  public void generateLake(Random random, World world, BlockPos pos) {
    while (pos.getY() > 5 && world.isAirBlock(pos)) {
      pos = pos.down();
    }
    if (pos.getY() <= 4) {
      return;
    }

    pos = pos.add(-8, 0, -8);
    pos = pos.down(4);

    boolean[] grid = new boolean[16 * 16 * 8];

    int spots = random.nextInt(4) + 4;
    for (int i = 0; i < spots; i++) {
      double xr = random.nextDouble() * 6 + 3;
      double yr = random.nextDouble() * 4 + 2;
      double zr = random.nextDouble() * 6 + 3;

      double xp = random.nextDouble() * (16 - xr - 2) + 1 + xr / 2;
      double yp = random.nextDouble() * (8 - yr - 4) + 2 + yr / 2;
      double zp = random.nextDouble() * (16 - zr - 2) + 1 + zr / 2;

      for (int xx = 1; xx < 15; xx++) {
        for (int zz = 1; zz < 15; zz++) {
          for (int yy = 1; yy < 7; yy++) {
            double xd = (xx - xp) / (xr / 2);
            double yd = (yy - yp) / (yr / 2);
            double zd = (zz - zp) / (zr / 2);
            double d = xd * xd + yd * yd + zd * zd;
            if (d < 1) {
              grid[(xx * 16 + zz) * 8 + yy] = true;
            }
          }
        }
      }
    }

    for (int xx = 0; xx < 16; xx++) {
      for (int zz = 0; zz < 16; zz++) {
        for (int yy = 0; yy < 8; yy++) {
          boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
              (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy])
              || (xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy])
              || (zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy])
              || (zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy])
              || (yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1])
              || (yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)])
          );

          if (check) {
            Material m = world.getBlockState(pos.add(xx, yy, zz)).getBlock().getMaterial();
            if (yy >= 4 && m.isLiquid()) {
              return;
            }
            //if (yy < 4 && !m.isSolid() && world.getBlockState(pos.add(xx, yy, zz)).getBlock() != liquid.getBlock()) {
              //return;
            //}
          }
        }
      }
    }

    for (int xx = 0; xx < 16; xx++) {
      for (int zz = 0; zz < 16; zz++) {
        for (int yy = 0; yy < 8; yy++) {
          if (grid[(xx * 16 + zz) * 8 + yy]) {
            world.setBlockState(pos.add(xx, yy, zz),
                                yy >= 4 ? Blocks.air.getDefaultState()
                                        : liquid, 2);
          }
        }
      }
    }
/*
    for (int xx = 0; xx < 16; xx++) {
      for (int zz = 0; zz < 16; zz++) {
        for (int yy = 4; yy < 8; yy++) {
          if (grid[(xx * 16 + zz) * 8 + yy]) {
            BlockPos grassPos = pos.add(xx, yy - 1, zz);

            if (world.getBlockState(grassPos).getBlock() == Blocks.DIRT && world.getBrightness(LightLayer.SKY, pos.add(xx, yy, zz)) > 0) {
              Biome b = world.getBiome(grassPos);
              if (b.topMaterial.getBlock() == Blocks.MYCELIUM) {
                world.setBlock(grassPos, Blocks.MYCELIUM.defaultBlockState(), Block.UPDATE_CLIENTS);
              } else {
                world.setBlock(grassPos, Blocks.FOLIAGE.defaultBlockState(), Block.UPDATE_CLIENTS);
              }
            }
          }
        }
      }
    }
*/
    // generate slime blocks around
    for (int xx = 0; xx < 16; xx++) {
      for (int zz = 0; zz < 16; zz++) {
        for (int yy = 0; yy < 8; yy++) {
          boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
              (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy])
              || (xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy])
              || (zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy])
              || (zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy])
              || (yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1])
              || (yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)])
          );

          if (check) {
            if ((yy < 4 || random.nextInt(2) != 0) && world.getBlockState(pos.add(xx, yy, zz)).getBlock().getMaterial().isSolid()) {
              // bottom of the lake?
              if(world.getBlockState(pos.add(xx, yy+1, zz)).getBlock().getMaterial().isLiquid()) {
                if(random.nextInt(10) == 0) {
                  world.setBlockState(pos.add(xx, yy, zz), lakeBottomBlock, 2);
                }
              }
              // no, around the lake
              else if(slimeBlocks.length > 0) {
                int r = random.nextInt(slimeBlocks.length);
                world.setBlockState(pos.add(xx, yy, zz), slimeBlocks[r], 2);
              }
            }
          }
        }
      }
    }
  }
}
