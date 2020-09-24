package slimeknights.tconstruct.world.worldgen.islands.overworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

import java.util.Random;

public class SlimeIslandPiece extends TemplateStructurePiece {

  private final String templateName;
  private final SlimeIslandVariant variant;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

  //private static final SlimeTree SLIME_TREE = new SlimeTree(SlimeGrassBlock.FoliageType.BLUE,true);
  //private static final SlimeTree PURPLE_BLUE_SLIME_TREE = new SlimeTree(SlimeGrassBlock.FoliageType.PURPLE,true);

  public SlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation) {
    this(templateManager, variant, templateName, templatePosition, rotation, Mirror.NONE);
  }

  public SlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.slimeIslandPiece, 0);
    this.templateName = templateName;
    this.variant = variant;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.loadTemplate(templateManager);
  }

  public SlimeIslandPiece(TemplateManager templateManager, CompoundNBT nbt) {
    super(TinkerStructures.slimeIslandPiece, nbt);
    this.templateName = nbt.getString("Template");
    this.variant = SlimeIslandVariant.getVariantFromIndex(nbt.getInt("Variant"));
    this.rotation = Rotation.valueOf(nbt.getString("Rot"));
    this.mirror = Mirror.valueOf(nbt.getString("Mi"));
    this.numberOfTreesPlaced = nbt.getInt("NumberOfTreesPlaced");
    this.loadTemplate(templateManager);
  }

  private void loadTemplate(TemplateManager templateManager) {
    Template template = templateManager.getTemplateDefaulted(new ResourceLocation("tconstruct:slime_islands/" + this.variant.getString() + "/" + this.templateName));
    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    this.setup(template, this.templatePosition, placementsettings);
  }

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
  protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
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
      case "tconstruct:slime_vine":
        if (this.variant.getVine() != null) {
          if (rand.nextBoolean()) {
            this.placeVine(worldIn, pos, rand, this.variant.getVine());
          }
          else {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
          }
        }
        else {
          worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        }
        break;
      case "tconstruct:slime_tree":
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

        /*ConfiguredFeature<SlimeTreeFeatureConfig, ?> treeFeature = null;

        if (rand.nextBoolean() && this.numberOfTreesPlaced < 3) {
          switch (this.variant) {
            case BLUE:
            case GREEN:
              treeFeature = PURPLE_BLUE_SLIME_TREE.getSlimeTreeFeature(rand, false);
              break;
            case PURPLE:
              treeFeature = SLIME_TREE.getSlimeTreeFeature(rand, false);
              break;
            default:
              throw new IllegalStateException("Unexpected variant: " + this.variant);
          }

          if (treeFeature != null && worldIn instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)worldIn;
            treeFeature.func_236265_a_(serverWorld, serverWorld.func_241112_a_(), this.chunkGenerator, rand, pos);
          }
        }*/

        worldIn.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), 2);

        this.numberOfTreesPlaced++;
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

  private void placeVine(IWorld worldIn, BlockPos pos, Random random, BlockState vineToPlace) {
    for (Direction direction : Direction.values()) {
      if (direction != Direction.DOWN && SlimeVineBlock.canAttachTo(worldIn, pos.offset(direction), direction)) {
        worldIn.setBlockState(pos, vineToPlace.with(SlimeVineBlock.getPropertyFor(direction), Boolean.TRUE), 2);
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

  @Override
  public boolean func_230383_a_(ISeedReader world, StructureManager manager, ChunkGenerator generator, Random rand, MutableBoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;
    return super.func_230383_a_(world, manager, generator, rand, bounds, chunk, pos);
  }
}
