package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.fluid.FluidMolten;

public class FluidsClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    super.registerModels();

    registerFluidModels(TinkerFluids.searedStone);
    registerFluidModels(TinkerFluids.obsidian);
    registerFluidModels(TinkerFluids.clay);
    registerFluidModels(TinkerFluids.dirt);
    registerFluidModels(TinkerFluids.gold);
    registerFluidModels(TinkerFluids.emerald);
    registerFluidModels(TinkerFluids.glass);

    registerFluidModels(TinkerFluids.milk);
    registerFluidModels(TinkerFluids.blueslime);
    registerFluidModels(TinkerFluids.purpleSlime);
    registerFluidModels(TinkerFluids.blood);
  }

  @SubscribeEvent
  public void registerTextures(TextureStitchEvent.Pre event) {
    // ensures fluid textures are registered even if our fluids are non-default
    TextureMap map = event.getMap();
    map.registerSprite(FluidColored.ICON_LiquidStill);
    map.registerSprite(FluidColored.ICON_LiquidFlowing);
    map.registerSprite(FluidColored.ICON_MilkStill);
    map.registerSprite(FluidColored.ICON_MilkFlowing);
    map.registerSprite(FluidColored.ICON_StoneStill);
    map.registerSprite(FluidColored.ICON_StoneFlowing);
    map.registerSprite(FluidMolten.ICON_MetalStill);
    map.registerSprite(FluidMolten.ICON_MetalFlowing);
  }

  @Override
  public void registerFluidModels(Fluid fluid) {
    if(fluid == null) {
      return;
    }

    Block block = fluid.getBlock();
    if(block != null) {
      Item item = Item.getItemFromBlock(block);
      FluidStateMapper mapper = new FluidStateMapper(fluid);

      // item-model
      if(item != Items.AIR) {
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
