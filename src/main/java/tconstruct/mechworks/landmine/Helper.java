package tconstruct.mechworks.landmine;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * 
 * @author fuj1n
 * 
 */
public class Helper
{

    public static ForgeDirection convertMetaToForgeOrientation (int metadata)
    {
        switch (metadata)
        {
        case 6:
            return ForgeDirection.DOWN;
        case 7:
            return ForgeDirection.UP;
        case 1:
            return ForgeDirection.WEST;
        case 3:
            return ForgeDirection.NORTH;
        case 2:
            return ForgeDirection.EAST;
        case 4:
            return ForgeDirection.SOUTH;
        case 5:
            return ForgeDirection.DOWN;
        case 0:
            return ForgeDirection.UP;
        }

        return ForgeDirection.UNKNOWN;
    }

    public static void renderInventoryCube (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
    }

}
