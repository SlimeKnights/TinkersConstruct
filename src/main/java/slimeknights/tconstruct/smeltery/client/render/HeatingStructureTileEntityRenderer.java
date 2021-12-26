package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderState.LineState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.controller.HeatingStructureTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock.StructureData;

import java.util.OptionalDouble;

public class HeatingStructureTileEntityRenderer extends TileEntityRenderer<HeatingStructureTileEntity> {
  private static final RenderType ERROR_BLOCK = RenderType.create(
    "lines", DefaultVertexFormats.POSITION_COLOR, 1, 256,
    RenderType.State.builder()
                    .setLineState(new LineState(OptionalDouble.empty()))
                    .setLayeringState(RenderState.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderState.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderState.COLOR_DEPTH_WRITE)
                    .setDepthTestState(RenderState.NO_DEPTH_TEST)
                    .createCompositeState(false));

  private static final float ITEM_SCALE = 15f/16f;
  public HeatingStructureTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(HeatingStructureTileEntity smeltery, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    World world = smeltery.getLevel();
    if (world == null) return;
    BlockState state = smeltery.getBlockState();
    StructureData structure = smeltery.getStructure();
    boolean structureValid = state.getValue(ControllerBlock.IN_STRUCTURE) && structure != null;

    // render erroring block, done whether in the structure or not
    BlockPos errorPos = smeltery.getErrorPos();
    if (errorPos != null && Minecraft.getInstance().player != null) {
      // either we must be holding the book, or the structure must be erroring and it be within 10 seconds of last update
      boolean highlightError = smeltery.isHighlightError();
      if ((!structureValid && highlightError) || smeltery.showDebugBlockBorder(Minecraft.getInstance().player)) {
        // distance check, 512 is the squared length of the diagonal of a max size structure
        BlockPos pos = smeltery.getBlockPos();
        BlockPos playerPos = Minecraft.getInstance().player.blockPosition();
        int dx = playerPos.getX() - pos.getX();
        int dz = playerPos.getZ() - pos.getZ();
        if ((dx * dx + dz * dz) < 512) {
          // color will be yellow if the structure is valid (expanding), red if invalid
          IVertexBuilder vertexBuilder = buffer.getBuffer(highlightError ? ERROR_BLOCK : RenderType.LINES);
          WorldRenderer.renderShape(matrices, vertexBuilder, VoxelShapes.block(), errorPos.getX() - pos.getX(), errorPos.getY() - pos.getY(), errorPos.getZ() - pos.getZ(), 1f, structureValid ? 1f : 0f, 0f, 0.5f);
        }
      }
    }

    // if no structure, nothing else to do
    if (!structureValid) {
      return;
    }

    // relevant positions
    BlockPos pos = smeltery.getBlockPos();
    BlockPos minPos = structure.getMinInside();
    BlockPos maxPos = structure.getMaxInside();

    // offset to make rendering min pos relative
    matrices.pushPose();
    matrices.translate(minPos.getX() - pos.getX(), minPos.getY() - pos.getY(), minPos.getZ() - pos.getZ());
    // render tank fluids, use minPos for brightness
    SmelteryTankRenderer.renderFluids(matrices, buffer, smeltery.getTank(), minPos, maxPos, WorldRenderer.getLightColor(world, minPos));

    // render items
    int xd = 1 + maxPos.getX() - minPos.getX();
    int zd = 1 + maxPos.getZ() - minPos.getZ();
    int layer = xd * zd;
    Direction facing = state.getValue(ControllerBlock.FACING);
    Quaternion itemRotation = Vector3f.YP.rotationDegrees(-90.0F * (float)facing.get2DDataValue());
    MeltingModuleInventory inventory = smeltery.getMeltingInventory();
    Minecraft mc = Minecraft.getInstance();
    ItemRenderer itemRenderer = mc.getItemRenderer();
    int max = Config.CLIENT.maxSmelteryItemQuads.get();
    if (max != 0) {
      int quadsRendered = 0;
      for (int i = 0; i < inventory.getSlots(); i++) {
        ItemStack stack = inventory.getStackInSlot(i);
        if (!stack.isEmpty()) {
          // calculate position inside the smeltery from slot index
          int height = i / layer;
          int layerIndex = i % layer;
          int offsetX = layerIndex % xd;
          int offsetZ = layerIndex / xd;
          BlockPos itemPos = minPos.offset(offsetX, height, offsetZ);

          // offset to the slot position in the structure, scale, and rotate the item
          matrices.pushPose();
          matrices.translate(offsetX + 0.5f, height + 0.5f, offsetZ + 0.5f);
          matrices.mulPose(itemRotation);
          matrices.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
          IBakedModel model = itemRenderer.getModel(stack, world, null);
          itemRenderer.render(stack, TransformType.NONE, false, matrices, buffer, WorldRenderer.getLightColor(world, itemPos), OverlayTexture.NO_OVERLAY, model);
          matrices.popPose();

          // done as quads rather than items as its not that expensive to draw blocks, items are the problem
          if (max != -1) {
            // builtin has no quads, lets pretend its 100 as they are more expensive
            if (model.isCustomRenderer()) {
              quadsRendered += 100;
            } else {
              // not setting the seed on the random and ignoring the forge layered model stuff means this is just an estimate, but since this is for the sake of performance its not a huge deal for it to be exact
              for (Direction direction : Direction.values()) {
                quadsRendered += model.getQuads(null, direction, TConstruct.RANDOM, EmptyModelData.INSTANCE).size();
              }
              quadsRendered += model.getQuads(null, null, TConstruct.RANDOM, EmptyModelData.INSTANCE).size();
            }
            if (quadsRendered > max) {
              break;
            }
          }
        }
      }
    }

    matrices.popPose();
  }

  @Override
  public boolean shouldRenderOffScreen(HeatingStructureTileEntity tile) {
    return tile.getBlockState().getValue(ControllerBlock.IN_STRUCTURE) && tile.getStructure() != null;
  }
}
