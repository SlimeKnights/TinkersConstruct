package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeVineBlock;

import java.util.Random;

public class SlimeIslandPiece extends TemplateStructurePiece {

  private final String templateName;
  private final String color;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;

  private static final BlueSlimeTree blueSlimeTree = new BlueSlimeTree(true);
  private static final PurpleSlimeTree purpleSlimeTree = new PurpleSlimeTree(true);

  public SlimeIslandPiece(TemplateManager templateManager, String color, String templateName, BlockPos templatePosition, Rotation rotation) {
    this(templateManager, color, templateName, templatePosition, rotation, Mirror.NONE);
  }

  public SlimeIslandPiece(TemplateManager templateManager, String color, String templateName, BlockPos templatePosition, Rotation rotation, Mirror mirror) {
    super(TinkerWorld.SLIME_ISLAND_PIECE, 0);
    this.templateName = templateName;
    this.color = color;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.loadTemplate(templateManager);
  }

  public SlimeIslandPiece(TemplateManager templateManager, CompoundNBT nbt) {
    super(TinkerWorld.SLIME_ISLAND_PIECE, nbt);
    this.templateName = nbt.getString("Template");
    this.color = nbt.getString("Color");
    this.rotation = Rotation.valueOf(nbt.getString("Rot"));
    this.mirror = Mirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(TemplateManager templateManager) {
    Template template = templateManager.getTemplateDefaulted(new ResourceLocation("tconstruct:slime_islands/" + this.color + "/" + this.templateName));
    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    this.setup(template, this.templatePosition, placementsettings);
  }

  /**
   * (abstract) Helper method to read subclass data from NBT
   */
  @Override
  protected void readAdditional(CompoundNBT tagCompound) {
    super.readAdditional(tagCompound);
    tagCompound.putString("Template", this.templateName);
    tagCompound.putString("Color", this.color);
    tagCompound.putString("Rot", this.placeSettings.getRotation().name());
    tagCompound.putString("Mi", this.placeSettings.getMirror().name());
    tagCompound.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
    switch (this.color) {
      case "blue":
        this.handleBlueIsland(function, pos, worldIn, rand);
        return;
      case "green":
        this.handleGreenIsland(function, pos, worldIn, rand);
        break;
      case "purple":
        this.handlePurpleIsland(function, pos, worldIn, rand);
        break;
    }
  }

  private void handleBlueIsland(String function, BlockPos pos, IWorld worldIn, Random random) {
    this.handleCommonIsland(function, pos, worldIn, random);

    if ("tconstruct:lake_bottom".equals(function)) {
      worldIn.setBlockState(pos, TinkerWorld.blue_blue_slime_grass.getDefaultState(), 2);
    }
  }

  private void handleGreenIsland(String function, BlockPos pos, IWorld worldIn, Random random) {
    this.handleCommonIsland(function, pos, worldIn, random);

    if ("tconstruct:lake_bottom".equals(function)) {
      worldIn.setBlockState(pos, TinkerWorld.blue_green_slime_grass.getDefaultState(), 2);
    }
  }

  private void handlePurpleIsland(String function, BlockPos pos, IWorld worldIn, Random random) {
    if ("tconstruct:lake_bottom".equals(function)) {
      worldIn.setBlockState(pos, TinkerWorld.purple_purple_slime_grass.getDefaultState(), 2);
    }
    else if ("tconstruct:slime_fluid".equals(function)) {
      if (TinkerFluids.purple_slime_fluid_block.get() != null) {
        worldIn.setBlockState(pos, TinkerFluids.purple_slime_fluid_block.get().getDefaultState(), 2);
      }
      else {
        worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
      }
    }
    else if ("tconstruct:congealed_slime".equals(function)) {
      worldIn.setBlockState(pos, TinkerCommons.congealed_purple_slime.getDefaultState(), 2);
    }
    else if ("tconstruct:slime_vine".equals(function)) {
      if (random.nextBoolean()) {
        this.placeVine(worldIn, pos, random, TinkerWorld.purple_slime_vine);
      }
      else {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
      }
    }
    else if ("tconstruct:slime_tree".equals(function)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

      if (random.nextBoolean() && this.numberOfTreesPlaced < 3) {
        AbstractTreeFeature<NoFeatureConfig> treeFeature = blueSlimeTree.getTreeFeature(random);
        if (treeFeature != null) {
          treeFeature.place(worldIn, worldIn.getChunkProvider().getChunkGenerator(), random, pos, IFeatureConfig.NO_FEATURE_CONFIG);
        }
      }

      this.numberOfTreesPlaced++;
    }
    else if ("tconstruct:slime_tall_grass".equals(function)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

      if (random.nextBoolean()) {
        if (random.nextBoolean()) {
          BlockState state = TinkerWorld.blue_slime_fern.getDefaultState();

          if (TinkerWorld.blue_slime_fern.isValidPosition(state, worldIn, pos)) {
            worldIn.setBlockState(pos, state, 2);
          }
        }
        else {
          BlockState state = TinkerWorld.blue_slime_tall_grass.getDefaultState();

          if (TinkerWorld.blue_slime_tall_grass.isValidPosition(state, worldIn, pos)) {
            worldIn.setBlockState(pos, state, 2);
          }
        }
      }
    }
  }

  private void handleCommonIsland(String function, BlockPos pos, IWorld worldIn, Random random) {
    if ("tconstruct:slime_fluid".equals(function)) {
      if (TinkerFluids.blue_slime_fluid_block.get() != null) {
        worldIn.setBlockState(pos, TinkerFluids.blue_slime_fluid_block.get().getDefaultState(), 2);
      }
      else {
        worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
      }
    }
    else if ("tconstruct:congealed_slime".equals(function)) {
      if (random.nextBoolean()) {
        worldIn.setBlockState(pos, TinkerCommons.congealed_blue_slime.getDefaultState(), 2);
      }
      else {
        worldIn.setBlockState(pos, TinkerCommons.congealed_green_slime.getDefaultState(), 2);
      }
    }
    else if ("tconstruct:slime_vine".equals(function)) {
      if (random.nextBoolean()) {
        this.placeVine(worldIn, pos, random, TinkerWorld.blue_slime_vine);
      }
      else {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
      }
    }
    else if ("tconstruct:slime_tree".equals(function)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

      if (random.nextBoolean() && this.numberOfTreesPlaced < 3) {
        AbstractTreeFeature<NoFeatureConfig> treeFeature = purpleSlimeTree.getTreeFeature(random);
        if (treeFeature != null) {
          treeFeature.place(worldIn, worldIn.getChunkProvider().getChunkGenerator(), random, pos, IFeatureConfig.NO_FEATURE_CONFIG);
        }
      }

      this.numberOfTreesPlaced++;
    }
    else if ("tconstruct:slime_tall_grass".equals(function)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

      if (random.nextBoolean()) {
        if (random.nextBoolean()) {
          BlockState state = TinkerWorld.purple_slime_fern.getDefaultState();

          if (TinkerWorld.purple_slime_fern.isValidPosition(state, worldIn, pos)) {
            worldIn.setBlockState(pos, state, 2);
          }
        }
        else {
          BlockState state = TinkerWorld.purple_slime_tall_grass.getDefaultState();

          if (TinkerWorld.purple_slime_tall_grass.isValidPosition(state, worldIn, pos)) {
            worldIn.setBlockState(pos, state, 2);
          }
        }
      }
    }
  }

  private void placeVine(IWorld worldIn, BlockPos pos, Random random, Block vineToPlace) {
    for (Direction direction : Direction.values()) {
      System.out.println("try place vine at pos: " + pos + " direction: " + direction + " can attach to: " + SlimeVineBlock.canAttachTo(worldIn, pos, direction) + " state: " + worldIn.getBlockState(pos));
      if (direction != Direction.DOWN && SlimeVineBlock.canAttachTo(worldIn, pos.offset(direction), direction)) {
        System.out.println("place vine at pos: " + pos);
        worldIn.setBlockState(pos, vineToPlace.getDefaultState().with(SlimeVineBlock.getPropertyFor(direction), Boolean.valueOf(true)), 2);
      }
    }

    BlockPos pos1 = pos;

    for (int size = random.nextInt(8); size >= 0; size++) {
      if (!(worldIn.getBlockState(pos1).getBlock() instanceof SlimeVineBlock)) {
        break;
      }

      ((SlimeVineBlock) worldIn.getBlockState(pos1).getBlock()).grow(worldIn, random, pos1, worldIn.getBlockState(pos1));

      pos1 = pos1.down();
    }
  }
}
