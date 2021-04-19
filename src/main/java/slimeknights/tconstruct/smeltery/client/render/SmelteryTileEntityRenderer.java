package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery.StructureData;

public class SmelteryTileEntityRenderer extends BlockEntityRenderer<SmelteryTileEntity> {
 private static final float ITEM_SCALE = 15f/16f;
  public SmelteryTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(SmelteryTileEntity smeltery, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
    World world = smeltery.getWorld();
    if (world == null) return;
    BlockState state = smeltery.getCachedState();
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
    SmelteryTankRenderer.renderFluids(matrices, buffer, smeltery.getTank(), minPos, maxPos, WorldRenderer.getLightmapCoordinates(world, minPos));

    // render items
    int xd = 1 + maxPos.getX() - minPos.getX();
    int zd = 1 + maxPos.getZ() - minPos.getZ();
    int layer = xd * zd;
    Direction facing = state.get(ControllerBlock.FACING);
    Quaternion itemRotation = Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F * (float)facing.getHorizontal());
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
        matrices.multiply(itemRotation);
        matrices.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        MinecraftClient.getInstance().getItemRenderer()
                 .renderItem(stack, Mode.NONE, WorldRenderer.getLightmapCoordinates(world, itemPos),
                             OverlayTexture.DEFAULT_UV, matrices, buffer);
        matrices.pop();
      }
    }

    matrices.pop();
  }
}
