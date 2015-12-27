package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

public class FluidsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    for(Fluid fluid : TinkerFluids.fluids) {
      Block block = fluid.getBlock();
      Item item = Item.getItemFromBlock(block);
      FluidStateMapper mapper = new FluidStateMapper(fluid);
      // item-model
      ModelBakery.addVariantName(item);
      ModelLoader.setCustomMeshDefinition(item, mapper);
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
      this.location = new ModelResourceLocation(Util.getResource("molten_metal"), fluid.getName());
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
      return location;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
      return location;
    }
  }
}
