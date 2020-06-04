package slimeknights.tconstruct.world.worldgen.islands.nether;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;
import slimeknights.tconstruct.world.worldgen.trees.SlimeTree;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import java.util.Random;

public class NetherSlimeIslandPiece extends TemplateStructurePiece {

  private final String templateName;
  private final SlimeIslandVariant variant;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator<?> chunkGenerator;

  private static final SlimeTree magmaSlimeTree = new SlimeTree(SlimeGrassBlock.FoliageType.ORANGE, false);

  public NetherSlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation) {
    this(templateManager, variant, templateName, templatePosition, rotation, Mirror.NONE);
  }

  public NetherSlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation, Mirror mirror) {
    super(TinkerWorld.NETHER_SLIME_ISLAND_PIECE, 0);
    this.templateName = templateName;
    this.variant = variant;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.loadTemplate(templateManager);
  }

  public NetherSlimeIslandPiece(TemplateManager templateManager, CompoundNBT nbt) {
    super(TinkerWorld.NETHER_SLIME_ISLAND_PIECE, nbt);
    this.templateName = nbt.getString("Template");
    this.variant = SlimeIslandVariant.getVariantFromIndex(nbt.getInt("Variant"));
    this.rotation = Rotation.valueOf(nbt.getString("Rot"));
    this.mirror = Mirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(TemplateManager templateManager) {
    Template template = templateManager.getTemplateDefaulted(new ResourceLocation("tconstruct:slime_islands/" + this.variant.getName() + "/" + this.templateName));
    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
    this.setup(template, this.templatePosition, placementsettings);
  }

  /**
   * (abstract) Helper method to read subclass data from NBT
   */
  @Override
  protected void readAdditional(CompoundNBT tagCompound) {
    super.readAdditional(tagCompound);
    tagCompound.putString("Template", this.templateName);
    tagCompound.putInt("Variant", this.variant.getIndex());
    tagCompound.putString("Rot", this.placeSettings.getRotation().name());
    tagCompound.putString("Mi", this.placeSettings.getMirror().name());
    tagCompound.putInt("NumberOfTreesPlaced", this.numberOfTreesPlaced);
  }

  @Override
  protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
    switch (function) {
      case "tconstruct:lake_bottom":
        worldIn.setBlockState(pos, this.variant.getLakeBottom(), 2);
        break;
      case "tconstruct:slime_fluid":
        worldIn.setBlockState(pos, this.variant.getLakeFluid(), 2);
        break;
      case "tconstruct:congealed_slime":
        int congealed_slime_random = rand.nextInt(this.variant.getCongealedSlime().length);
        worldIn.setBlockState(pos, this.variant.getCongealedSlime()[congealed_slime_random], 2);
        break;
      case "tconstruct:slime_tree":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

        if (rand.nextBoolean() && this.numberOfTreesPlaced < 3) {
          ConfiguredFeature<SlimeTreeFeatureConfig, ?> treeFeature = magmaSlimeTree.getSlimeTreeFeature(rand, false);

          if (treeFeature != null) {
            treeFeature.place(worldIn, this.chunkGenerator, rand, pos);
          }
        }

        this.numberOfTreesPlaced++;
        break;
      case "tconstruct:slime_vine":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        break;
      case "tconstruct:slime_tall_grass":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

        if (rand.nextBoolean()) {
          int slime_grass_random = rand.nextInt(this.variant.getTallGrass().length);
          BlockState state = this.variant.getTallGrass()[slime_grass_random];

          if (state.getBlock() instanceof SlimeTallGrassBlock) {
            if (((SlimeTallGrassBlock) state.getBlock()).isValidPosition(state, worldIn, pos)) {
              worldIn.setBlockState(pos, state, 2);
            }
          }
        }
        break;
    }
  }

  @Override
  public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
    this.chunkGenerator = chunkGenerator;

    if (this.isLava(worldIn, this.templatePosition.up()) && this.isLava(worldIn, this.templatePosition.up().north()) && this.isLava(worldIn, this.templatePosition.up().east()) && this.isLava(worldIn, this.templatePosition.up().south()) && this.isLava(worldIn, this.templatePosition.up().west())) {
      return super.create(worldIn, chunkGenerator, randomIn, structureBoundingBoxIn, chunkPosIn);
    } else {
      return false;
    }
  }

  private boolean isLava(IWorld world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }
}
