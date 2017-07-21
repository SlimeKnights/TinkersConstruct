package slimeknights.tconstruct.world.village.smeltery;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.world.village.TinkerVillage;

public class ComponentSmeltery extends Village {

  public ComponentSmeltery() {
  }

  public ComponentSmeltery(Start start, int type, Random rand, StructureBoundingBox structureboundingbox, EnumFacing facing) {
    super(start, type);
    this.setCoordBaseMode(facing);
    this.boundingBox = structureboundingbox;
  }

  public static ComponentSmeltery createPiece(Start start, List<StructureComponent> pieces, Random rand, int p1, int p2, int p3, EnumFacing facing, int p4) {
    StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 3, 7, facing);
    return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentSmeltery(start, p4, rand, structureboundingbox, facing) : null;
  }

  /**
   * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
   * Mineshafts at the end, it adds Fences...
   */
  @Override
  public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
    if(this.averageGroundLvl < 0) {
      this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

      if(this.averageGroundLvl < 0) {
        return true;
      }

      this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 2, 0);
    }

    IBlockState stone_brick = Blocks.STONEBRICK.getDefaultState();
    IBlockState seared_brick = TinkerSmeltery.searedBlock.getDefaultState().withProperty(BlockSeared.TYPE, BlockSeared.SearedType.BRICK);
    IBlockState casting_table = TinkerSmeltery.castingBlock.getDefaultState().withProperty(BlockCasting.TYPE, BlockCasting.CastingType.TABLE);
    IBlockState casting_basin = TinkerSmeltery.castingBlock.getDefaultState().withProperty(BlockCasting.TYPE, BlockCasting.CastingType.BASIN);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 0, 6, stone_brick, stone_brick, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 1, 0, 0, 5, stone_brick, stone_brick, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 1, 8, 0, 5, stone_brick, stone_brick, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 9, 3, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 1, 6, 2, 5, seared_brick, seared_brick, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 5, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

    this.setBlockState(worldIn, casting_basin, 1, 1, 2, structureBoundingBoxIn);
    this.setBlockState(worldIn, casting_table, 1, 1, 4, structureBoundingBoxIn);
    this.setBlockState(worldIn, casting_basin, 7, 1, 2, structureBoundingBoxIn);
    this.setBlockState(worldIn, casting_table, 7, 1, 4, structureBoundingBoxIn);

    for(int l = 1; l < 6; ++l) {
      for(int i1 = 0; i1 < 9; ++i1) {
        this.clearCurrentPositionBlocksUpwards(worldIn, i1, 9, l, structureBoundingBoxIn);
        this.replaceAirAndLiquidDownwards(worldIn, stone_brick, i1, -1, l, structureBoundingBoxIn);
      }
    }

    for(int l = 0; l < 7; ++l) {
      for(int i1 = 1; i1 < 8; ++i1) {
        this.clearCurrentPositionBlocksUpwards(worldIn, i1, 9, l, structureBoundingBoxIn);
        this.replaceAirAndLiquidDownwards(worldIn, stone_brick, i1, -1, l, structureBoundingBoxIn);
      }
    }

    this.spawnVillagers(worldIn, structureBoundingBoxIn, 3, 1, 3, 1);

    return true;
  }

  @Override
  protected VillagerProfession chooseForgeProfession(int count, net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
    return TinkerVillage.villagerProfession_smeltery;
  }

}
