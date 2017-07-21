package slimeknights.tconstruct.world.village.workshop;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.block.BlockToolTable.TableTypes;
import slimeknights.tconstruct.world.village.TinkerVillage;

public class ComponentToolWorkshop extends Village {

  public ComponentToolWorkshop() {
  }

  public ComponentToolWorkshop(Start start, int type, Random rand, StructureBoundingBox structureboundingbox, EnumFacing facing) {
    super(start, type);
    this.setCoordBaseMode(facing);
    this.boundingBox = structureboundingbox;
  }

  public static ComponentToolWorkshop createPiece(Start start, List<StructureComponent> pieces, Random rand, int p1, int p2, int p3, EnumFacing facing, int p4) {
    StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 7, 6, 7, facing);
    return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentToolWorkshop(start, p4, rand, structureboundingbox, facing) : null;
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

      this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4, 0);
    }

    IBlockState air = Blocks.AIR.getDefaultState();
    IBlockState cobblestone = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
    IBlockState oak_fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
    IBlockState planks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
    IBlockState wool = this.getBiomeSpecificBlockState(Blocks.WOOL.getDefaultState());
    IBlockState log = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 0, 6, cobblestone, cobblestone, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 6, 5, 6, oak_fence, oak_fence, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 5, 0, 5, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 2, 4, 0, 4, wool, wool, false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 0, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 4, 6, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 0, 6, 4, 0, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 6, 6, 4, 6, log, log, false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 1, 5, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 5, 1, 0, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 5, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 6, 5, 1, 6, planks, planks, false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 3, 5, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 5, 3, 0, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 5, planks, planks, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 6, 5, 3, 6, planks, planks, false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 0, 4, 5, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 0, 5, 4, 0, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 1, 6, 4, 5, log, log, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 6, 5, 4, 6, log, log, false);

    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 5, 5, 5, air, air, false);
    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 5, 4, 5, planks, planks, false);

    this.setBlockState(worldIn, air, 3, 1, 0, structureBoundingBoxIn);
    this.setBlockState(worldIn, air, 3, 2, 0, structureBoundingBoxIn);
    this.placeTorch(worldIn, EnumFacing.NORTH, 3, 3, 1, structureBoundingBoxIn);

    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
    this.setBlockState(worldIn, planks, 2, 2, 0, structureBoundingBoxIn);
    this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 3, 1, 0, EnumFacing.NORTH);
    this.setBlockState(worldIn, planks, 4, 2, 0, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);

    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 1, 2, 6, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
    this.setBlockState(worldIn, planks, 3, 2, 6, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 6, structureBoundingBoxIn);

    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 1, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
    this.setBlockState(worldIn, planks, 0, 2, 3, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 5, structureBoundingBoxIn);

    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 1, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 2, structureBoundingBoxIn);
    this.setBlockState(worldIn, planks, 6, 2, 3, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 4, structureBoundingBoxIn);
    this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);

    IBlockState ladder = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
    this.setBlockState(worldIn, ladder, 3, 1, 5, structureBoundingBoxIn);
    this.setBlockState(worldIn, ladder, 3, 2, 5, structureBoundingBoxIn);
    this.setBlockState(worldIn, ladder, 3, 3, 5, structureBoundingBoxIn);
    this.setBlockState(worldIn, ladder, 3, 4, 5, structureBoundingBoxIn);

    IBlockState toolstation = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.ToolStation);
    IBlockState patternchest = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.PatternChest);
    IBlockState partbuilder = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.PartBuilder);
    IBlockState craftingstation = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.CraftingStation);
    IBlockState stenciltable = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.StencilTable);
    IBlockState partchest = TinkerTools.toolTables.getDefaultState().withProperty(BlockToolTable.TABLES, TableTypes.PartChest);

    this.setBlockState(worldIn, toolstation, 1, 1, 1, structureBoundingBoxIn); // TODO: REPLACE WITH LOOT TABLE?
    this.generateTable(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 2, TinkerVillage.PATTERN_CHEST_LOOT_TABLE, patternchest, TinkerTools.toolTables);
    this.setBlockState(worldIn, partbuilder, 1, 1, 3, structureBoundingBoxIn); // TODO: REPLACE WITH LOOT TABLE?
    this.generateTable(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 4, TinkerVillage.CRAFTING_STATION_LOOT_TABLE, craftingstation, TinkerTools.toolTables);
    this.setBlockState(worldIn, stenciltable, 1, 1, 5, structureBoundingBoxIn);

    this.generateTable(worldIn, structureBoundingBoxIn, randomIn, 4, 1, 5, TinkerVillage.PART_CHEST_LOOT_TABLE, partchest, TinkerTools.toolTables);

    IBlockState piston = Blocks.PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH);
    this.setBlockState(worldIn, piston, 5, 1, 5, structureBoundingBoxIn);

    for(int l = 0; l < 7; ++l) {
      for(int i1 = 0; i1 < 7; ++i1) {
        this.clearCurrentPositionBlocksUpwards(worldIn, i1, 9, l, structureBoundingBoxIn);
        this.replaceAirAndLiquidDownwards(worldIn, cobblestone, i1, -1, l, structureBoundingBoxIn);
      }
    }

    this.spawnVillagers(worldIn, structureBoundingBoxIn, 3, 1, 3, 1);

    return true;
  }

  protected boolean generateTable(World worldIn, StructureBoundingBox structureBoundingBoxIn, Random randomIn, int x, int y, int z, ResourceLocation lootIn, @Nonnull IBlockState stateIn, Block blockIn) {
    BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
    return this.generateTable(worldIn, structureBoundingBoxIn, randomIn, blockpos, lootIn, stateIn, blockIn);
  }

  protected boolean generateTable(World worldIn, StructureBoundingBox structureBoundingBoxIn, Random randomIn, BlockPos posIn, ResourceLocation lootIn, @Nonnull IBlockState stateIn, Block blockIn) {
    if(structureBoundingBoxIn.isVecInside(posIn) && worldIn.getBlockState(posIn).getBlock() != blockIn) {

      worldIn.setBlockState(posIn, stateIn, 2);
      TileEntity tileentity = worldIn.getTileEntity(posIn);

      if(tileentity instanceof TileTable) {
        ((TileTable) tileentity).setLootTable(lootIn, randomIn.nextLong());
      }

      return true;
    }
    else {
      return false;
    }
  }

  @Override
  protected VillagerProfession chooseForgeProfession(int count, net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
    return TinkerVillage.villagerProfession_tools;
  }
}
