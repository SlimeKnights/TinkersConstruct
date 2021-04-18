package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.fluid.volume.FluidEntry;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.MantleFluid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;

import java.util.function.Function;

public class FluidClientEvents extends ClientEventBase {

  @Override
  public void onInitializeClient() {
    for (FluidEntry.FluidFloatingEntry registryFluidId : FluidKeys.getFloatingFluidIds()) {
      if(registryFluidId.getId().getNamespace().equals(TConstruct.modID)) {
        FluidKey fluidKey = FluidKeys.get(registryFluidId);
        MantleFluid fluid = ((MantleFluid) fluidKey.getRawFluid());
        setupFluidRendering(
          fluid.getStill(),
          fluid.getFlowing(),
          fluidKey.flowingSpriteId,
          fluidKey.spriteId,
          fluidKey.renderColor
        );
      }
    }

    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.skySlime.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.skySlime.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.enderSlime.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.enderSlime.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.liquidSoul.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.liquidSoul.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.moltenSoulsteel.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.moltenSoulsteel.getFlowing(), RenderLayer.getTranslucent());
  }

  public static void setupFluidRendering(final Fluid still, final Fluid flowing, Identifier flowingSpriteId, final Identifier stillSpriteId, final int color) {
//    final Identifier stillSpriteId = new Identifier(flowingSpriteId.getNamespace(), "block/" + flowingSpriteId.getPath() + "_still");
//    final Identifier flowingSpriteId = new Identifier(stillSpriteId.getNamespace(), "block/" + stillSpriteId.getPath() + "_flow");

    // If they're not already present, add the sprites to the block atlas
    ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
      registry.register(stillSpriteId);
      registry.register(flowingSpriteId);
    });

    final Identifier fluidId = Registry.FLUID.getId(still);
    final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

    final Sprite[] fluidSprites = {null, null};

    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
      @Override
      public Identifier getFabricId() {
        return listenerId;
      }

      /**
       * Get the sprites from the block atlas when resources are reloaded
       */
      @Override
      public void apply(ResourceManager resourceManager) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        fluidSprites[0] = atlas.apply(stillSpriteId);
        fluidSprites[1] = atlas.apply(flowingSpriteId);
      }
    });

    // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
    final FluidRenderHandler renderHandler = new FluidRenderHandler() {
      @Override
      public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
        return fluidSprites;
      }

      @Override
      public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
        return color;
      }
    };

    FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
    FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
  }
}
