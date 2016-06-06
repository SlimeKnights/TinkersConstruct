package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

public class FluidsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    registerFluidModels(TinkerFluids.searedStone);
    registerFluidModels(TinkerFluids.obsidian);
    registerFluidModels(TinkerFluids.clay);
    registerFluidModels(TinkerFluids.dirt);
    registerFluidModels(TinkerFluids.gold);
    registerFluidModels(TinkerFluids.emerald);

    registerFluidModels(TinkerFluids.milk);
    registerFluidModels(TinkerFluids.blueslime);
    registerFluidModels(TinkerFluids.purpleSlime);
    registerFluidModels(TinkerFluids.blood);
  }

  @Override
  public void registerFluidModels(Fluid fluid) {
    if(fluid == null) return;

    Block block = fluid.getBlock();
    if(block != null) {
      Item item = Item.getItemFromBlock(block);
      FluidStateMapper mapper = new FluidStateMapper(fluid);

      // item-model
      if(item != null) {
        ModelLoader.registerItemVariants(item);
        ModelLoader.setCustomMeshDefinition(item, mapper);
      }
      // block-model
      ModelLoader.setCustomStateMapper(block, mapper);
    }
  }

  public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

    public final Fluid fluid;
    public final ModelResourceLocation location;

    public FluidStateMapper(Fluid fluid) {
      this.fluid = fluid;

      // have each block hold its fluid per nbt? hm
      this.location = new ModelResourceLocation(Util.getResource("fluid_block"), fluid.getName());
    }

    @Nonnull
    @Override
    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
      return location;
    }

    @Nonnull
    @Override
    public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
      return location;
    }
  }
}
