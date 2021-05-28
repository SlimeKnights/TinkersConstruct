package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.HeatingStructureTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock.StructureData;

public class HeatingStructureTileEntityRenderer extends TileEntityRenderer<HeatingStructureTileEntity> {
 private static final float ITEM_SCALE = 15f/16f;
  public HeatingStructureTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(HeatingStructureTileEntity smeltery, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    World world = smeltery.getWorld();
    if (world == null) return;
    BlockState state = smeltery.getBlockState();
    if (!state.get(ControllerBlock.IN_STRUCTURE)) return;
    StructureData structure = smeltery.getStructure();
    if (structure == null) return;

    // relevant positions
    BlockPos pos = smeltery.getPos();
    BlockPos minPos = structure.getMinInside();
    BlockPos maxPos = structure.getMaxInside();

    // offset to make rendering min pos relative
    matrices.push();
    matrices.translate(minPos.getX() - pos.getX(), minPos.getY() - pos.getY(), minPos.getZ() - pos.getZ());
    // render tank fluids, use minPos for brightness
    SmelteryTankRenderer.renderFluids(matrices, buffer, smeltery.getTank(), minPos, maxPos, WorldRenderer.getCombinedLight(world, minPos));

    // render items
    int xd = 1 + maxPos.getX() - minPos.getX();
    int zd = 1 + maxPos.getZ() - minPos.getZ();
    int layer = xd * zd;
    Direction facing = state.get(ControllerBlock.FACING);
    Quaternion itemRotation = Vector3f.YP.rotationDegrees(-90.0F * (float)facing.getHorizontalIndex());
    MeltingModuleInventory inventory = smeltery.getMeltingInventory();
    for (int i = 0; i < inventory.getSlots(); i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (!stack.isEmpty()) {
        // calculate position inside the smeltery from slot index
        int height = i / layer;
        int layerIndex = i % layer;
        int offsetX = layerIndex % xd;
        int offsetZ = layerIndex / xd;
        BlockPos itemPos = minPos.add(offsetX, height, offsetZ);

        // offset to the slot position in the structure, scale, and rotate the item
        matrices.push();
        matrices.translate(offsetX + 0.5f, height + 0.5f, offsetZ + 0.5f);
        matrices.rotate(itemRotation);
        matrices.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        Minecraft.getInstance().getItemRenderer()
                 .renderItem(stack, TransformType.NONE, WorldRenderer.getCombinedLight(world, itemPos),
                             OverlayTexture.NO_OVERLAY, matrices, buffer);
        matrices.pop();
      }
    }

    matrices.pop();
  }

  @Override
  public boolean isGlobalRenderer(HeatingStructureTileEntity tile) {
    return tile.getBlockState().get(ControllerBlock.IN_STRUCTURE) && tile.getStructure() != null;
  }
}
