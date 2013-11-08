package tconstruct.client.block;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.SignalBusLogic;
import tconstruct.blocks.logic.SignalTerminalLogic;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import static tconstruct.blocks.SignalTerminal.TerminalGeometry.*;

public class SignalTerminalRender implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        // Render X-
        renderer.setRenderBounds(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
        this.renderStandardBlock(block, metadata, renderer);

        renderer.setRenderBounds(center_min, center_min, center_min, center_max, center_max, center_max);
        this.renderStandardBlock(block, metadata, renderer);

        renderer.setRenderBounds(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
        renderer.setOverrideBlockTexture(SignalTerminalLogic.getChannelIcon(0));
        this.renderStandardBlock(block, metadata, renderer);
        renderer.clearOverrideBlockTexture();
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        int sidesRendered = 0;

        if (modelId == renderID)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (!(te instanceof SignalTerminalLogic))
            {
                // Render X-
                renderer.setRenderBounds(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(center_min, center_min, center_min, center_max, center_max, center_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.setOverrideBlockTexture(SignalTerminalLogic.getChannelIcon(0));
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                return true;
            }
            byte[] connectedSides = ((SignalTerminalLogic) te).getConnectedSides();
            Icon channelIcons[] = ((SignalTerminalLogic) te).getSideIcons();

            // Center
            renderer.setRenderBounds(center_min, center_min, center_min, center_max, center_max, center_max);
            renderer.renderStandardBlock(block, x, y, z);

            if (connectedSides[ForgeDirection.WEST.ordinal()] != -1)
            {
                // Render X-
                renderer.setRenderBounds(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.WEST.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.EAST.ordinal()] != -1)
            {
                //Extend X+
                renderer.setRenderBounds(plate_high_min, plate_width_min, plate_width_min, plate_high_max, plate_width_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_high_min, channel_width_min, channel_width_min, channel_high_max, channel_width_max, channel_width_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.EAST.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.SOUTH.ordinal()] != -1)
            {
                //Extend Z-
                renderer.setRenderBounds(plate_width_min, plate_width_min, plate_low_min, plate_width_max, plate_width_max, plate_low_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_width_min, channel_width_min, channel_low_min, channel_width_max, channel_width_max, channel_low_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.SOUTH.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.NORTH.ordinal()] != -1)
            {
                //Extend Z+
                renderer.setRenderBounds(plate_width_min, plate_width_min, plate_high_min, plate_width_max, plate_width_max, plate_high_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_width_min, channel_width_min, channel_high_min, channel_width_max, channel_width_max, channel_high_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.NORTH.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.DOWN.ordinal()] != -1)
            {
                //Extend Y-
                renderer.setRenderBounds(plate_width_min, plate_low_min, plate_width_min, plate_width_max, plate_low_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_width_min, channel_low_min, channel_width_min, channel_width_max, channel_low_max, channel_width_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.DOWN.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.UP.ordinal()] != -1)
            {
                //Extend Y+
                renderer.setRenderBounds(plate_width_min, plate_high_min, plate_width_min, plate_width_max, plate_high_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_width_min, channel_high_min, channel_width_min, channel_width_max, channel_high_max, channel_width_max);
                renderer.setOverrideBlockTexture(channelIcons[ForgeDirection.UP.ordinal()]);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

                sidesRendered++;
            }

            if (sidesRendered == 0)
            {
                // Render X-
                renderer.setRenderBounds(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.setOverrideBlockTexture(SignalTerminalLogic.getChannelIcon(0));
                renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();

            }
            return true;
        }
        return false;
    }

    private void renderStandardBlock (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory ()
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return this.renderID;
    }
}
