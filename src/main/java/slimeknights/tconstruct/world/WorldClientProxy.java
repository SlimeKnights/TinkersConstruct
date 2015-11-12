package slimeknights.tconstruct.world;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeSapling;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;
import slimeknights.tconstruct.world.client.CustomStateMap;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class WorldClientProxy extends ClientProxy {
  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  @Override
  public void preInit() {
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(slimeColorizer);

    super.preInit();
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
}
