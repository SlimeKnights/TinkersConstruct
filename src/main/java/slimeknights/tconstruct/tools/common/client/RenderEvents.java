package slimeknights.tconstruct.tools.common.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import slimeknights.tconstruct.library.tools.DualToolHarvestUtils;
import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderEvents implements IResourceManagerReloadListener {

  private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png"); // GuiIngame.widgetsTexPath
  private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];

  @SubscribeEvent
  public void renderExtraBlockBreak(RenderWorldLastEvent event) {
    PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;
    EntityPlayer player = Minecraft.getMinecraft().player;
    World world = player.getEntityWorld();

    ItemStack tool = player.getHeldItemMainhand();

    // AOE preview
    if(!tool.isEmpty()) {
      Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if(renderEntity != null) {
        double distance = controllerMP.getBlockReachDistance();
        RayTraceResult mop = renderEntity.rayTrace(distance, event.getPartialTicks());
        if(mop != null) {
          tool = DualToolHarvestUtils.getItemstackToUse(player, world.getBlockState(mop.getBlockPos()));
          if(tool.getItem() instanceof IAoeTool) {
            ImmutableList<BlockPos> extraBlocks = ((IAoeTool) tool.getItem()).getAOEBlocks(tool, world, player, mop.getBlockPos());
            for(BlockPos pos : extraBlocks) {
              event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
            }
          }
        }
      }
    }

    // extra-blockbreak animation
    if(controllerMP.isHittingBlock) {
      tool = DualToolHarvestUtils.getItemstackToUse(player, world.getBlockState(controllerMP.currentBlock));
      if(tool.getItem() instanceof IAoeTool && ((IAoeTool) tool.getItem()).isAoeHarvestTool()) {
        BlockPos pos = controllerMP.currentBlock;
        drawBlockDamageTexture(Tessellator.getInstance(),
                               Tessellator.getInstance().getBuffer(),
                               player,
                               event.getPartialTicks(),
                               world,
                               ((IAoeTool) tool.getItem()).getAOEBlocks(tool, world, player, pos));
      }
    }
  }

  // RenderGlobal.drawBlockDamageTexture
  public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilder, Entity entityIn, float partialTicks, World world, List<BlockPos> blocks) {
    double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
    double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
    double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;

    TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
    int progress = (int) (Minecraft.getMinecraft().playerController.curBlockDamageMP * 10f) - 1; // 0-10

    if(progress < 0) {
      return;
    }

    renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    //preRenderDamagedBlocks BEGIN
    GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
    GlStateManager.enableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
    GlStateManager.doPolygonOffset(-3.0F, -3.0F);
    GlStateManager.enablePolygonOffset();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableAlpha();
    GlStateManager.pushMatrix();
    //preRenderDamagedBlocks END

    bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    bufferBuilder.setTranslation(-d0, -d1, -d2);
    bufferBuilder.noColor();

    for(BlockPos blockpos : blocks) {
      double d3 = blockpos.getX() - d0;
      double d4 = blockpos.getY() - d1;
      double d5 = blockpos.getZ() - d2;
      Block block = world.getBlockState(blockpos).getBlock();
      TileEntity te = world.getTileEntity(blockpos);
      boolean hasBreak = block instanceof BlockChest || block instanceof BlockEnderChest
                         || block instanceof BlockSign || block instanceof BlockSkull;
      if(!hasBreak) {
        hasBreak = te != null && te.canRenderBreaking();
      }

      if(!hasBreak) {
        IBlockState iblockstate = world.getBlockState(blockpos);

        if(iblockstate.getBlock().getMaterial(iblockstate) != Material.AIR) {
          TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[progress];
          BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
          blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, world);
        }
      }
    }

    tessellatorIn.draw();
    bufferBuilder.setTranslation(0.0D, 0.0D, 0.0D);
    // postRenderDamagedBlocks BEGIN
    GlStateManager.disableAlpha();
    GlStateManager.doPolygonOffset(0.0F, 0.0F);
    GlStateManager.disablePolygonOffset();
    GlStateManager.enableAlpha();
    GlStateManager.depthMask(true);
    GlStateManager.popMatrix();
    // postRenderDamagedBlocks END
  }

  @SubscribeEvent
  public void handRenderEvent(RenderSpecificHandEvent event) {
    EntityPlayer player = Minecraft.getMinecraft().player;

    // when drawing a bow or crossbow, stop the other hand from rendering like vanilla bows
    if(player.isHandActive()) {
      ItemStack stack = player.getActiveItemStack();
      if(!stack.isEmpty() && stack.getItemUseAction() == EnumAction.BOW) {
        if (event.getHand() != player.getActiveHand()) {
          event.setCanceled(true);
        }
        return;
      }
    }

    ItemStack mainStack = player.getHeldItemMainhand();
    RayTraceResult rt = Minecraft.getMinecraft().objectMouseOver;
    if(!mainStack.isEmpty()
       && rt != null
       && rt.typeOfHit == RayTraceResult.Type.BLOCK
       && DualToolHarvestUtils.shouldUseOffhand(player, rt.getBlockPos(), mainStack)) {

      event.setCanceled(true);

      EnumHand hand;
      ItemStack itemStack;
      if(event.getHand() == EnumHand.MAIN_HAND) {
        hand = EnumHand.OFF_HAND;
        itemStack = player.getHeldItemOffhand();
      }
      else {
        hand = EnumHand.MAIN_HAND;
        itemStack = player.getHeldItemMainhand();
      }

      ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();
      itemRenderer.renderItemInFirstPerson(
          Minecraft.getMinecraft().player,
          event.getPartialTicks(),
          event.getInterpolatedPitch(),
          hand,
          event.getSwingProgress(),
          itemStack,
          event.getEquipProgress());
    }
  }

  @SubscribeEvent
  public void onFovEvent(FOVUpdateEvent event) {
    ItemStack stack = event.getEntity().getActiveItemStack();

    if(!stack.isEmpty()) {
      float zoom = 0f;
      float progress = 0f;

      if(stack.getItem() == TinkerRangedWeapons.longBow) {
        zoom = 0.35f;
        progress = TinkerRangedWeapons.longBow.getDrawbackProgress(stack, event.getEntity());
      }

      if(zoom > 0) {
        event.setNewfov(1f - (progress * progress) * zoom);
      }
    }
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

    for(int i = 0; i < this.destroyBlockIcons.length; ++i) {
      this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
    }
  }
}
