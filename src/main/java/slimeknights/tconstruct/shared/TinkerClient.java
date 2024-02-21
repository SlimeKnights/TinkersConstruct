package slimeknights.tconstruct.shared;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.client.book.TinkerBook;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.tables.client.PatternGuiTextureLoader;

import java.util.function.Consumer;

/**
 * This class should only be referenced on the client side
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public class TinkerClient {
  /**
   * Called by TConstruct to handle any client side logic that needs to run during the constructor
   */
  public static void onConstruct() {
    TinkerBook.initBook();
    // needs to register listeners early enough for minecraft to load
    PatternGuiTextureLoader.init();
    ModifierIconManager.init();

    // add the recipe cache invalidator to the client
    Consumer<RecipesUpdatedEvent> recipesUpdated = event -> RecipeCacheInvalidator.reload(true);
    MinecraftForge.EVENT_BUS.addListener(recipesUpdated);

    // register datagen serializers
    ISpriteTransformer.SERIALIZER.registerDeserializer(RecolorSpriteTransformer.NAME, RecolorSpriteTransformer.DESERIALIZER);
    GreyToSpriteTransformer.init();
    IColorMapping.SERIALIZER.registerDeserializer(GreyToColorMapping.NAME, GreyToColorMapping.DESERIALIZER);
  }

  @SubscribeEvent
  static void renderBlockOverlay(RenderBlockOverlayEvent event) {
    BlockState state = event.getBlockState();
    if (state.is(TinkerTags.Blocks.TRANSPARENT_OVERLAY)) {
      Minecraft minecraft = Minecraft.getInstance();
      assert minecraft.level != null;
      assert minecraft.player != null;
      BlockPos pos = event.getBlockPos();
      float width = minecraft.player.getBbWidth() * 0.8F;
      // check collision of the block again, for non-full blocks
      if (Shapes.joinIsNotEmpty(state.getShape(minecraft.level, pos).move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(AABB.ofSize(minecraft.player.getEyePosition(), width, 1.0E-6D, width)), BooleanOp.AND)) {
        // this is for the most part a clone of the vanilla logic from ScreenEffectRenderer with some changes mentioned below

        TextureAtlasSprite texture = minecraft.getBlockRenderer().getBlockModelShaper().getTexture(state, minecraft.level, pos);
        RenderSystem.setShaderTexture(0, texture.atlas().location());
        // changed: shader using pos tex
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        // change: handle brightness based on renderWater, and enable blend
        float brightness = minecraft.player.getBrightness();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0f);

        // draw the quad
        float u0 = texture.getU0();
        float u1 = texture.getU1();
        float v0 = texture.getV0();
        float v1 = texture.getV1();
        Matrix4f matrix4f = event.getPoseStack().last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        // change: dropped color, see above
        bufferbuilder.vertex(matrix4f, -1, -1, -0.5f).uv(u1, v1).endVertex();
        bufferbuilder.vertex(matrix4f, 1, -1, -0.5f).uv(u0, v1).endVertex();
        bufferbuilder.vertex(matrix4f, 1, 1, -0.5f).uv(u0, v0).endVertex();
        bufferbuilder.vertex(matrix4f, -1, 1, -0.5f).uv(u1, v0).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        // changed: disable blend
        RenderSystem.disableBlend();
      }
      event.setCanceled(true);
    }
  }
}
