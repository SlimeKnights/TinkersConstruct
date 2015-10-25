package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockGrass;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Iterator;

import slimeknights.tconstruct.world.client.SlimeColorizer;

public class BlockSlimeGrass extends BlockGrass {
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    /*
    int i = 0;
    int j = 0;
    int k = 0;
    int l;

    for (Iterator iterator = BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1)).iterator(); iterator.hasNext(); k += l & 255)
    {
      BlockPos.MutableBlockPos mutableblockpos = (BlockPos.MutableBlockPos)iterator.next();
      BiomeGenBase biome = worldIn.getBiomeGenForCoords(mutableblockpos);
      double temp = (double) MathHelper.clamp_float(biome.getFloatTemperature(mutableblockpos), 0.0F, 1.0F);
      double hum = (double)MathHelper.clamp_float(biome.getFloatRainfall(), 0.0F, 1.0F);
      l = SlimeColorizer.getColor(temp, hum);
      i += (l & 16711680) >> 16;
      j += (l & 65280) >> 8;
    }

    return (i / 9 & 255) << 16 | (j / 9 & 255) << 8 | k / 9 & 255;*/
    float loop = 250;
    float x = Math.abs((loop - (Math.abs(pos.getX())%(2*loop)))/loop);
    float z = Math.abs((loop - (Math.abs(pos.getZ())%(2*loop)))/loop);

    // x,z are [-1,1] now


    if(x < z) {
      float tmp = x;
      x = z;
      z = tmp;
    }



    return SlimeColorizer.getColor(x,z);
  }
}
