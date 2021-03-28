package slimeknights.tconstruct.world.worldgen.islands;

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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import java.util.Random;

public class SlimeIslandPiece extends TemplateStructurePiece {

  private final String templateName;
  private final SlimeIslandVariant variant;
  private final Rotation rotation;
  private final Mirror mirror;
  private int numberOfTreesPlaced;
  private ChunkGenerator chunkGenerator;

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
    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(this.variant.getStructureProcessor());
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

        if (rand.nextBoolean() && this.numberOfTreesPlaced < 3) {
          ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> treeFeature = this.variant.getConfiguredTreeFeature();

          if (treeFeature != null && worldIn instanceof ISeedReader) {
            ISeedReader seedReader = (ISeedReader) worldIn;
            treeFeature.generate(seedReader, this.chunkGenerator, rand, pos);
          }
        }

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

    // grow the vine a few times to start
    BlockPos vinePos = pos;
    for (int size = random.nextInt(8); size >= 0; size--) {
      BlockState state = worldIn.getBlockState(vinePos);
      if (!(state.getBlock() instanceof SlimeVineBlock)) {
        break;
      }
      ((SlimeVineBlock) state.getBlock()).grow(worldIn, random, vinePos, state);
      vinePos = vinePos.down();
    }
  }

  @Override
  public boolean func_230383_a_(ISeedReader world, StructureManager manager, ChunkGenerator generator, Random rand, MutableBoundingBox bounds, ChunkPos chunk, BlockPos pos) {
    this.chunkGenerator = generator;

    if (this.variant == SlimeIslandVariant.BLOOD) {
      BlockPos up = this.templatePosition.up();

      if (this.isLava(world, up)) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
          if (!this.isLava(world, up.offset(dir))) {
            return false;
          }
        }

        return super.func_230383_a_(world, manager, generator, rand, bounds, chunk, pos);
      }

      return false;
    }
    else {
      return super.func_230383_a_(world, manager, generator, rand, bounds, chunk, pos);
    }
  }

  private boolean isLava(IWorld world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }
}
