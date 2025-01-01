package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.TinkerRenderTypes;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock.StructureData;

public class HeatingStructureBlockEntityRenderer implements BlockEntityRenderer<HeatingStructureBlockEntity> {
  private static final float ITEM_SCALE = 15f/16f;

  public HeatingStructureBlockEntityRenderer(Context context) {}

  @Override
  public void render(HeatingStructureBlockEntity smeltery, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    Level world = smeltery.getLevel();
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
          VertexConsumer vertexBuilder = buffer.getBuffer(highlightError ? TinkerRenderTypes.ERROR_BLOCK : RenderType.LINES);
          LevelRenderer.renderShape(matrices, vertexBuilder, Shapes.block(), errorPos.getX() - pos.getX(), errorPos.getY() - pos.getY(), errorPos.getZ() - pos.getZ(), 1f, structureValid ? 1f : 0f, 0f, 0.5f);
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
    SmelteryTankRenderer.renderFluids(matrices, buffer, smeltery.getTank(), minPos, maxPos, LevelRenderer.getLightColor(world, minPos));

    // render items
    int xd = 1 + maxPos.getX() - minPos.getX();
    int zd = 1 + maxPos.getZ() - minPos.getZ();
    int layer = xd * zd;
    Direction facing = state.getValue(ControllerBlock.FACING);
    Quaternionf itemRotation = Axis.YP.rotationDegrees(-90.0F * (float)facing.get2DDataValue());
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
          BakedModel model = itemRenderer.getModel(stack, world, null, 0);
          itemRenderer.render(stack, TinkerItemDisplays.MELTER, false, matrices, buffer, LevelRenderer.getLightColor(world, itemPos), OverlayTexture.NO_OVERLAY, model);
          matrices.popPose();

          // done as quads rather than items as its not that expensive to draw blocks, items are the problem
          if (max != -1) {
            // builtin has no quads, lets pretend its 100 as they are more expensive
            if (model.isCustomRenderer()) {
              quadsRendered += 100;
            } else {
              RandomSource random = smeltery.getLevel().getRandom();
              // not setting the seed on the random and ignoring the forge layered model stuff means this is just an estimate, but since this is for the sake of performance its not a huge deal for it to be exact
              for (Direction direction : Direction.values()) {
                quadsRendered += model.getQuads(null, direction, random, ModelData.EMPTY, null).size();
              }
              quadsRendered += model.getQuads(null, null, random, ModelData.EMPTY, null).size();
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
  public boolean shouldRenderOffScreen(HeatingStructureBlockEntity tile) {
    return tile.getBlockState().getValue(ControllerBlock.IN_STRUCTURE) && tile.getStructure() != null;
  }
}
