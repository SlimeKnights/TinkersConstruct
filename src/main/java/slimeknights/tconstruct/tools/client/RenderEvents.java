package slimeknights.tconstruct.tools.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import java.util.List;

import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

@SideOnly(Side.CLIENT)
public class RenderEvents implements IResourceManagerReloadListener {

  private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png"); // GuiIngame.widgetsTexPath
  private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];

  @SubscribeEvent
  public void onRenderGUI(RenderGameOverlayEvent.Pre event) {
    // we're only interested in the inventory
    if(event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
      return;
    }

    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    ItemStack stack = player.getCurrentEquippedItem();
    if(stack == null) {
      return;
    }

    // do we hold a tool that supports secondary item usage?
    if(stack.getItem() instanceof ToolCore && ((ToolCore) stack.getItem()).canUseSecondaryItem()) {
      int slot = ToolHelper.getSecondaryItemSlot(player);

      if(slot != player.inventory.currentItem) {
        // render the special border around the secondary item that would be used
        int x = event.resolution.getScaledWidth() / 2 - 90 + slot * 20 + 2;
        int y = event.resolution.getScaledHeight() - 16 - 3;

        // render a cool underlay thing
        GlStateManager.color(1,1,1,0.5f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(widgetsTexPath);
        Gui.drawScaledCustomSizeModalRect(x, y, 1, 23, 22, 22, 16, 16, 256,256);
      }
    }
  }

  @SubscribeEvent
  public void renderExtraBlockBreak(RenderWorldLastEvent event) {
    PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    World world = player.worldObj;
    // AOE preview
    if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IAoeTool) {
      MovingObjectPosition mop = player.rayTrace(controllerMP.getBlockReachDistance(), event.partialTicks);
      if(mop != null) {
        ItemStack stack = player.getCurrentEquippedItem();
        ImmutableList<BlockPos> extraBlocks = ((IAoeTool) stack.getItem()).getAOEBlocks(stack, world, player, mop
            .getBlockPos());
        for(BlockPos pos : extraBlocks) {
          event.context.drawSelectionBox(player, new MovingObjectPosition(new Vec3(0,0,0), null, pos), 0, event.partialTicks);
        }
      }
    }

    // extra-blockbreak animation
    if(controllerMP.isHittingBlock) {
      if(controllerMP.currentItemHittingBlock != null &&
         controllerMP.currentItemHittingBlock.getItem() instanceof IAoeTool &&
         ((IAoeTool) controllerMP.currentItemHittingBlock.getItem()).isAoeHarvestTool()) {
        ItemStack stack = controllerMP.currentItemHittingBlock;
        BlockPos pos = controllerMP.currentBlock;
        drawBlockDamageTexture(Tessellator.getInstance(),
                               Tessellator.getInstance().getWorldRenderer(),
                               player,
                               event.partialTicks,
                               world,
                               ((IAoeTool) stack.getItem()).getAOEBlocks(stack, world, player, pos));
      }
    }
  }

  // RenderGlobal.drawBlockDamageTexture
  public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, World world, List<BlockPos> blocks)
  {
    double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
    double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
    double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;

    TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
    int progress = (int)(Minecraft.getMinecraft().playerController.curBlockDamageMP*10f) - 1; // 0-10

    if(progress < 0)
      return;

      renderEngine.bindTexture(TextureMap.locationBlocksTexture);
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

      // todo: 1.8.8
      //worldRendererIn.startDrawingQuads();
      //worldRendererIn.setVertexFormat(DefaultVertexFormats.BLOCK);
      worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
      worldRendererIn.setTranslation(-d0, -d1, -d2);
      worldRendererIn.markDirty();

      for(BlockPos blockpos : blocks) {
        double d3 = (double)blockpos.getX() - d0;
        double d4 = (double)blockpos.getY() - d1;
        double d5 = (double)blockpos.getZ() - d2;
        Block block = world.getBlockState(blockpos).getBlock();
        TileEntity te = world.getTileEntity(blockpos);
        boolean hasBreak = block instanceof BlockChest || block instanceof BlockEnderChest
                           || block instanceof BlockSign || block instanceof BlockSkull;
        if (!hasBreak) hasBreak = te != null && te.canRenderBreaking();

        if (!hasBreak)
        {
            IBlockState iblockstate = world.getBlockState(blockpos);

            if (iblockstate.getBlock().getMaterial() != Material.air)
            {
              TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[progress];
              BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
              blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, world);
            }
        }
      }

      tessellatorIn.draw();
      worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
      // postRenderDamagedBlocks BEGIN
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
      // postRenderDamagedBlocks END
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

    for (int i = 0; i < this.destroyBlockIcons.length; ++i)
    {
      this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
    }
  }
}
