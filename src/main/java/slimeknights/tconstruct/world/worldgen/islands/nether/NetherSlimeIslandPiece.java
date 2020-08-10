package slimeknights.tconstruct.world.worldgen.islands.nether;

/*
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;
import slimeknights.tconstruct.world.worldgen.trees.SlimeTree;

import java.util.Random;

public class NetherSlimeIslandPiece extends TemplateStructurePiece {

  private final String templateName;
  private final SlimeIslandVariant variant;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

  private static final SlimeTree magmaSlimeTree = new SlimeTree(SlimeGrassBlock.FoliageType.ORANGE, false);

  public NetherSlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation) {
    this(templateManager, variant, templateName, templatePosition, rotation, Mirror.NONE);
  }

  public NetherSlimeIslandPiece(TemplateManager templateManager, SlimeIslandVariant variant, String templateName, BlockPos templatePosition, Rotation rotation, Mirror mirror) {
    super(TinkerStructures.netherSlimeIslandPiece, 0);
    this.templateName = templateName;
    this.variant = variant;
    this.templatePosition = templatePosition;
    this.rotation = rotation;
    this.mirror = mirror;
    this.numberOfTreesPlaced = 0;
    this.loadTemplate(templateManager);
  }

  public NetherSlimeIslandPiece(TemplateManager templateManager, CompoundNBT nbt) {
    super(TinkerStructures.netherSlimeIslandPiece, nbt);
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
          ConfiguredFeature<? extends BaseTreeFeatureConfig, ?> treeFeature = magmaSlimeTree.getSlimeTreeFeature(rand, false);

          if (treeFeature != null && worldIn instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)worldIn;
            treeFeature.func_236265_a_(serverWorld, serverWorld.func_241112_a_(), this.chunkGenerator, rand, pos);
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
  public boolean func_230383_a_(ISeedReader world, StructureManager manager, ChunkGenerator generator, Random rand, MutableBoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    BlockPos up = this.templatePosition.up();
    if (this.isLava(world, up)) {
      for (Direction dir : Plane.HORIZONTAL) {
        if (!this.isLava(world, up.offset(dir))) {
          return false;
        }
      }
      return super.func_230383_a_(world, manager, generator, rand, bounds, chunk, pos);
    }
    return false;
  }

  private boolean isLava(IWorld world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }
}
*/
