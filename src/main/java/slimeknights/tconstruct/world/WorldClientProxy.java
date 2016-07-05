package slimeknights.tconstruct.world;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.client.renderer.RenderTinkerSlime;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;
import slimeknights.tconstruct.world.block.BlockSlimeSapling;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;
import slimeknights.tconstruct.world.client.CustomStateMap;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;

public class WorldClientProxy extends ClientProxy {

  public static SlimeColorizer slimeColorizer = new SlimeColorizer();
  public static Minecraft minecraft = Minecraft.getMinecraft();

  @Override
  public void preInit() {
    ((IReloadableResourceManager) minecraft.getResourceManager()).registerReloadListener(slimeColorizer);


    // Entities
    RenderingRegistry.registerEntityRenderingHandler(EntityBlueSlime.class, RenderTinkerSlime.FACTORY_BlueSlime);

    super.preInit();
  }

  @Override
  public void init() {
    final BlockColors blockColors = minecraft.getBlockColors();

    // slime grass, slime tall grass, and slime leaves
    blockColors.registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            FoliageType type = state.getValue(BlockSlimeGrass.FOLIAGE);
            if(pos == null) {
              return SlimeColorizer.getColorStatic(type);
            }

            return SlimeColorizer.getColorForPos(pos, type);
          }
        },
        TinkerWorld.slimeGrass, TinkerWorld.slimeGrassTall);

    // leaves are a bit shifted, so they have slightly different tone than the grass they accompany
    blockColors.registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            FoliageType type = state.getValue(BlockSlimeGrass.FOLIAGE);
            if(pos == null) {
              return SlimeColorizer.getColorStatic(type);
            }

            return SlimeColorizer.getColorForPos(pos.add(SlimeColorizer.loop / 2, 0, SlimeColorizer.loop / 2), type);
          }
        },
        TinkerWorld.slimeLeaves);

    // slime vines don't use a foliage color state, so color them directly
    blockColors.registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            if(pos == null) {
              return SlimeColorizer.getColorStatic(BlockSlimeGrass.FoliageType.BLUE);
            }

            return SlimeColorizer.getColorForPos(pos.add(SlimeColorizer.loop / 2, 0, SlimeColorizer.loop / 2), BlockSlimeGrass.FoliageType.BLUE);
          }
        },
        TinkerWorld.slimeVineBlue1, TinkerWorld.slimeVineBlue2, TinkerWorld.slimeVineBlue3);
    blockColors.registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            if(pos == null) {
              return SlimeColorizer.getColorStatic(BlockSlimeGrass.FoliageType.PURPLE);
            }

            return SlimeColorizer.getColorForPos(pos.add(SlimeColorizer.loop / 2, 0, SlimeColorizer.loop / 2), BlockSlimeGrass.FoliageType.PURPLE);
          }
        },
        TinkerWorld.slimeVinePurple1, TinkerWorld.slimeVinePurple2, TinkerWorld.slimeVinePurple3);

    // item models simply pull the data from the block models, to make things easier for each separate type
    minecraft.getItemColors().registerItemColorHandler(
        new IItemColor() {
          @Override
          public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
          }
        },
        TinkerWorld.slimeGrass, TinkerWorld.slimeGrassTall, TinkerWorld.slimeLeaves,
        TinkerWorld.slimeVineBlue1, TinkerWorld.slimeVineBlue2, TinkerWorld.slimeVineBlue3,
        TinkerWorld.slimeVinePurple1, TinkerWorld.slimeVinePurple2, TinkerWorld.slimeVinePurple3);

    super.init();
  }

  @Override
  protected void registerModels() {
    // blocks
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeGrass, (new StateMap.Builder()).ignore(BlockSlimeGrass.FOLIAGE).build());
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeLeaves, (new StateMap.Builder())
        .ignore(BlockSlimeGrass.FOLIAGE, BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build());
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeGrassTall, (new StateMap.Builder()).ignore(BlockSlimeGrass.FOLIAGE).build());
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeSapling, (new StateMap.Builder()).ignore(BlockSlimeSapling.STAGE, BlockSapling.TYPE).build());

    //ModelLoader.setCustomStateMapper(TinkerWorld.slimeVine, (new StateMap.Builder()).ignore(BlockSlimeVine.FOLIAGE).build());

    IStateMapper vineMap = new CustomStateMap("slime_vine");
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVineBlue1, vineMap);
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVinePurple1, vineMap);
    vineMap = new CustomStateMap("slime_vine_mid");
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVineBlue2, vineMap);
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVinePurple2, vineMap);
    vineMap = new CustomStateMap("slime_vine_end");
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVineBlue3, vineMap);
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeVinePurple3, vineMap);

    // items
    registerItemBlockMeta(TinkerWorld.slimeDirt);

    // slime grass
    Item grass = Item.getItemFromBlock(TinkerWorld.slimeGrass);
    for(BlockSlimeGrass.FoliageType type : BlockSlimeGrass.FoliageType.values()) {
      for(BlockSlimeGrass.DirtType dirt : BlockSlimeGrass.DirtType.values()) {
        String variant = String.format("%s=%s,%s=%s",
                                       BlockSlimeGrass.SNOWY.getName(),
                                       BlockSlimeGrass.SNOWY.getName(false),
                                       BlockSlimeGrass.TYPE.getName(),
                                       BlockSlimeGrass.TYPE.getName(dirt)
        );
        int meta = TinkerWorld.slimeGrass.getMetaFromState(TinkerWorld.slimeGrass.getDefaultState()
                                                                                 .withProperty(BlockSlimeGrass.TYPE, dirt)
                                                                                 .withProperty(BlockSlimeGrass.FOLIAGE, type));
        ModelLoader.setCustomModelResourceLocation(grass, meta, new ModelResourceLocation(grass.getRegistryName(), variant));
      }
    }

    // slime leaves
    Item leaves = Item.getItemFromBlock(TinkerWorld.slimeLeaves);
    for(BlockSlimeGrass.FoliageType type : BlockSlimeGrass.FoliageType.values()) {
      ModelLoader.setCustomModelResourceLocation(leaves, type.getMeta(), new ModelResourceLocation(leaves.getRegistryName(), "normal"));
    }

    IBlockState state = TinkerWorld.slimeSapling.getDefaultState();
    Item sapling = Item.getItemFromBlock(TinkerWorld.slimeSapling);
    ItemStack stack = new ItemStack(sapling, 1, TinkerWorld.slimeSapling.getMetaFromState(state.withProperty(BlockSlimeSapling.FOLIAGE, BlockSlimeGrass.FoliageType.BLUE)));
    registerItemModel(stack, "slime_sapling_blue");
    stack = new ItemStack(sapling, 1, TinkerWorld.slimeSapling.getMetaFromState(state.withProperty(BlockSlimeSapling.FOLIAGE, BlockSlimeGrass.FoliageType.PURPLE)));
    registerItemModel(stack, "slime_sapling_purple");
    stack = new ItemStack(sapling, 1, TinkerWorld.slimeSapling.getMetaFromState(state.withProperty(BlockSlimeSapling.FOLIAGE, BlockSlimeGrass.FoliageType.ORANGE)));
    registerItemModel(stack, "slime_sapling_orange");

    for(BlockSlimeGrass.FoliageType foliage : BlockSlimeGrass.FoliageType.values()) {
      state = TinkerWorld.slimeGrassTall.getDefaultState();
      state = state.withProperty(BlockTallSlimeGrass.FOLIAGE, foliage);
      state = state.withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.TALL_GRASS);
      stack = new ItemStack(TinkerWorld.slimeGrassTall, 0, TinkerWorld.slimeGrassTall.getMetaFromState(state));
      registerItemModel(stack, "slime_tall_grass");

      state = state.withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.FERN);
      stack = new ItemStack(TinkerWorld.slimeGrassTall, 0, TinkerWorld.slimeGrassTall.getMetaFromState(state));
      registerItemModel(stack, "slime_fern");
    }

    registerItemModel(new ItemStack(TinkerWorld.slimeVineBlue1), "slime_vine");
    registerItemModel(new ItemStack(TinkerWorld.slimeVineBlue2), "slime_vine_mid");
    registerItemModel(new ItemStack(TinkerWorld.slimeVineBlue3), "slime_vine_end");
    registerItemModel(new ItemStack(TinkerWorld.slimeVinePurple1), "slime_vine");
    registerItemModel(new ItemStack(TinkerWorld.slimeVinePurple2), "slime_vine_mid");
    registerItemModel(new ItemStack(TinkerWorld.slimeVinePurple3), "slime_vine_end");
  }

  @Override
  public void postInit() {
    super.postInit();

  }
}
