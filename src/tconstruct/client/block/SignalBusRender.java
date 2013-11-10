package tconstruct.client.block;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.blocks.logic.SignalBusLogic;
import tconstruct.blocks.SignalBus.Geometry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SignalBusRender implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //Base
        renderer.setRenderBounds(0.375D, 0.0D, 0.375D, 0.625D, 0.2D, 0.625D);
        this.renderStandardBlock(block, metadata, renderer);
        //Extend Z-
        renderer.setRenderBounds(0.375D, 0.0D, 0.0D, 0.625D, 0.2D, 0.375D);
        this.renderStandardBlock(block, metadata, renderer);
        //Extend Z+
        renderer.setRenderBounds(0.375D, 0.0D, 0.625D, 0.625D, 0.2D, 1D);
        this.renderStandardBlock(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean[] placedSides;
        boolean[] connectedSides;
        boolean[] corners;
        boolean didRender = false;
        if (modelId == renderID)
        {
            SignalBusLogic tile = (SignalBusLogic) world.getBlockTileEntity(x, y, z);
            placedSides = tile.placedSides();

            for (int i = 0; i < 6; ++i)
            {
                if (!placedSides[i])
                {
                    continue;
                }
                didRender = true;
                connectedSides = tile.connectedSides(ForgeDirection.getOrientation(i));
                corners = tile.getRenderCorners(ForgeDirection.getOrientation(i));

                renderFaceWithConnections(renderer, block, x, y, z, i, placedSides, connectedSides, corners);
            }
            if (!didRender)
            {
                double minX = Geometry.cable_width_min;
                double minY = Geometry.cable_low_offset;
                double minZ = Geometry.cable_width_min;
                double maxX = Geometry.cable_width_max;
                double maxY = Geometry.cable_low_height;
                double maxZ = Geometry.cable_width_max;

                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

        }
        return true;
    }

    private void renderFaceWithConnections (RenderBlocks renderer, Block block, int x, int y, int z, int side, boolean[] placed, boolean[] connectedSides, boolean[] corners)
    {
        double minX = 0D;
        double minY = 0D;
        double minZ = 0D;
        double maxX = 1D;
        double maxY = 1D;
        double maxZ = 1D;

        boolean[] renderDir = { (connectedSides[0] || placed[0] || corners[0]), (connectedSides[1] || placed[1] || corners[1]), (connectedSides[2] || placed[2] || corners[2]),
                (connectedSides[3] || placed[3] || corners[3]), (connectedSides[4] || placed[4] || corners[4]), (connectedSides[5] || placed[5] || corners[5]) };

        switch (side)
        {
        case 0: // DOWN
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minY = Geometry.cable_low_offset;
                minZ = Geometry.cable_width_min;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxY = Geometry.cable_low_height;
                maxZ = Geometry.cable_width_max;

                maxY += Geometry.zfight;

                minX = (corners[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_corner_max : maxX;

                renderer.setRenderBounds(minX, minY, minZ, Geometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(Geometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = Geometry.cable_width_min;
                minY = Geometry.cable_low_offset;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                maxX = Geometry.cable_width_max;
                maxY = Geometry.cable_low_height;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;

                minZ = (corners[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_corner_min : minZ;
                maxZ = (corners[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_corner_max : maxZ;

                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, Geometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, Geometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_width_min;
            minY = Geometry.cable_low_offset;
            minZ = Geometry.cable_width_min;
            maxX = Geometry.cable_width_max;
            maxY = Geometry.cable_low_height;
            maxZ = Geometry.cable_width_max;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 1: // UP
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minY = Geometry.cable_high_offset;
                minZ = Geometry.cable_width_min;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxY = Geometry.cable_high_height;
                maxZ = Geometry.cable_width_max;

                minY -= Geometry.zfight;

                minX = (corners[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_corner_max : maxX;

                renderer.setRenderBounds(minX, minY, minZ, Geometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(Geometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = Geometry.cable_width_min;
                minY = Geometry.cable_high_offset;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                maxX = Geometry.cable_width_max;
                maxY = Geometry.cable_high_height;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;

                minZ = (corners[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_corner_min : minZ;
                maxZ = (corners[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_corner_max : maxZ;

                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, Geometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, Geometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_width_min;
            minY = Geometry.cable_high_offset;
            minZ = Geometry.cable_width_min;
            maxX = Geometry.cable_width_max;
            maxY = Geometry.cable_high_height;
            maxZ = Geometry.cable_width_max;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 2: // NORTH
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minY = Geometry.cable_width_min;
                minZ = Geometry.cable_low_offset;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxY = Geometry.cable_width_max;
                maxZ = Geometry.cable_low_height;

                minX = (corners[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_corner_max : maxX;

                renderer.setRenderBounds(minX, minY, minZ, Geometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(Geometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = Geometry.cable_width_min;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minZ = Geometry.cable_low_offset;
                maxX = Geometry.cable_width_max;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxZ = Geometry.cable_low_height;

                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, Geometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, minZ, maxX, Geometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_width_min;
            minY = Geometry.cable_width_min;
            minZ = Geometry.cable_low_offset;
            maxX = Geometry.cable_width_max;
            maxY = Geometry.cable_width_max;
            maxZ = Geometry.cable_low_height;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 3: // SOUTH
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minY = Geometry.cable_width_min;
                minZ = Geometry.cable_high_offset;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxY = Geometry.cable_width_max;
                maxZ = Geometry.cable_high_height;

                minX = (corners[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_corner_max : maxX;

                renderer.setRenderBounds(minX, minY, minZ, Geometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(Geometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = Geometry.cable_width_min;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minZ = Geometry.cable_high_offset;
                maxX = Geometry.cable_width_max;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxZ = Geometry.cable_high_height;

                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, Geometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, minZ, maxX, Geometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_width_min;
            minY = Geometry.cable_width_min;
            minZ = Geometry.cable_high_offset;
            maxX = Geometry.cable_width_max;
            maxY = Geometry.cable_width_max;
            maxZ = Geometry.cable_high_height;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 4: // WEST
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = Geometry.cable_low_offset;
                minY = Geometry.cable_width_min;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                maxX = Geometry.cable_low_height;
                maxY = Geometry.cable_width_max;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;

                minZ += (placed[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_low_height : 0;
                maxZ -= (placed[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, Geometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, Geometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = Geometry.cable_low_offset;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minZ = Geometry.cable_width_min;
                maxX = Geometry.cable_low_height;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxZ = Geometry.cable_width_max;

                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, Geometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, minZ, maxX, Geometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_low_offset;
            minY = Geometry.cable_width_min;
            minZ = Geometry.cable_width_min;
            maxX = Geometry.cable_low_height;
            maxY = Geometry.cable_width_max;
            maxZ = Geometry.cable_width_max;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 5: // EAST
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = Geometry.cable_high_offset;
                minY = Geometry.cable_width_min;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                maxX = Geometry.cable_high_height;
                maxY = Geometry.cable_width_max;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;

                minZ += (placed[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_low_height : 0;
                maxZ -= (placed[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, Geometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, Geometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = Geometry.cable_high_offset;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
                minZ = Geometry.cable_width_min;
                maxX = Geometry.cable_high_height;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
                maxZ = Geometry.cable_width_max;

                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? Geometry.cable_low_height : 0;

                renderer.setRenderBounds(minX, Geometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(minX, minY, minZ, maxX, Geometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = Geometry.cable_high_offset;
            minY = Geometry.cable_width_min;
            minZ = Geometry.cable_width_min;
            maxX = Geometry.cable_high_height;
            maxY = Geometry.cable_width_max;
            maxZ = Geometry.cable_width_max;

            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        default:
            return;
        }

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
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return renderID;
    }
}
