package tconstruct.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tconstruct.library.tools.ToolCore;

public class ToolCoreRenderer implements IItemRenderer
{
    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        switch (type)
        {
        case ENTITY:
        case EQUIPPED:
        case EQUIPPED_FIRST_PERSON:
        case INVENTORY:
            return true;
        default:
            System.out.println("[TCon] Unhandled render case!");
        case FIRST_PERSON_MAP:
            return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return handleRenderType(item, type) & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
    }

    private static final int toolIcons = 10;

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data)
    {
        ToolCore tool = (ToolCore) item.getItem();

        boolean isInventory = type == ItemRenderType.INVENTORY;
        Entity ent = null;
        if (data.length > 1)
            ent = (Entity) data[1];

        int iconParts = toolIcons;//tool.getRenderPasses(item.getItemDamage());

        Icon[] tempParts = new Icon[iconParts];
        label:
        {
            if (!isInventory && ent instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) ent;
                ItemStack itemInUse = player.getItemInUse();
                if (itemInUse != null)
                {
                    int useCount = player.getItemInUseCount();
                    for (int i = iconParts; i-- > 0;)
                        tempParts[i] = tool.getIcon(item, i, player, itemInUse, useCount);
                    break label;
                }
            }
            for (int i = iconParts; i-- > 0;)
                tempParts[i] = tool.getIcon(item, i);
        }

        int count = 0;
        Icon[] parts = new Icon[iconParts];
        for (int i = 0; i < iconParts; ++i)
        {
            Icon part = tempParts[i];
            if (part == null)// || part == ToolCore.blankSprite | part == ToolCore.emptyIcon)
                ++count;
            else
                parts[i - count] = part;
        }
        iconParts -= count;

        if (iconParts <= 0)
        {
            iconParts = 1;
            // TODO: assign default sprite
            // parts = new Icon[]{ defaultSprite };
        }

        Tessellator tess = Tessellator.instance;
        float[] xMax = new float[iconParts];
        float[] yMin = new float[iconParts];
        float[] xMin = new float[iconParts];
        float[] yMax = new float[iconParts];
        int[] sheetWidth = new int[iconParts];
        int[] sheetHeight = new int[iconParts];
        float depth = 1f / 16f;

        float[] width = new float[iconParts];
        float[] height = new float[iconParts];
        float[] xDiff = new float[iconParts];
        float[] yDiff = new float[iconParts];
        float[] xSub = new float[iconParts];
        float[] ySub = new float[iconParts];
        for (int i = 0; i < iconParts; ++i)
        {
            Icon icon = parts[i];
            xMin[i] = icon.getMinU();
            xMax[i] = icon.getMaxU();
            yMin[i] = icon.getMinV();
            yMax[i] = icon.getMaxV();
            sheetWidth[i] = icon.getOriginX();
            sheetHeight[i] = icon.getOriginY();
            xDiff[i] = xMin[i] - xMax[i];
            yDiff[i] = yMin[i] - yMax[i];
            width[i] = sheetWidth[i] * xDiff[i];
            height[i] = sheetHeight[i] * yDiff[i];
            xSub[i] = 0.5f * (xMax[i] - xMin[i]) / sheetWidth[i];
            ySub[i] = 0.5f * (yMax[i] - yMin[i]) / sheetHeight[i];
        }
        GL11.glPushMatrix();

        if (type == ItemRenderType.INVENTORY)
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            tess.startDrawingQuads();
            for (int i = 0; i < iconParts; ++i)
            {
                tess.addVertexWithUV(0, 16, 0, xMin[i], yMax[i]);
                tess.addVertexWithUV(16, 16, 0, xMax[i], yMax[i]);
                tess.addVertexWithUV(16, 0, 0, xMax[i], yMin[i]);
                tess.addVertexWithUV(0, 0, 0, xMin[i], yMin[i]);
            }
            tess.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        else
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            switch (type)
            {
            case EQUIPPED_FIRST_PERSON:
                break;
            case EQUIPPED:
                GL11.glTranslatef(0, -4 / 16f, 0);
                break;
            case ENTITY:
                GL11.glTranslatef(0, 4 / 16f, 0);
                break;
            default:
            }

            tess.startDrawingQuads();
            tess.setNormal(0, 0, 1);
            for (int i = 0; i < iconParts; ++i)
            {
                tess.addVertexWithUV(0, 0, 0, xMax[i], yMax[i]);
                tess.addVertexWithUV(1, 0, 0, xMin[i], yMax[i]);
                tess.addVertexWithUV(1, 1, 0, xMin[i], yMin[i]);
                tess.addVertexWithUV(0, 1, 0, xMax[i], yMin[i]);
            }
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0, 0, -1);
            for (int i = 0; i < iconParts; ++i)
            {
                tess.addVertexWithUV(0, 1, -depth, xMax[i], yMin[i]);
                tess.addVertexWithUV(1, 1, -depth, xMin[i], yMin[i]);
                tess.addVertexWithUV(1, 0, -depth, xMin[i], yMax[i]);
                tess.addVertexWithUV(0, 0, -depth, xMax[i], yMax[i]);
            }
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(-1, 0, 0);
            int k;
            float pos;
            float iconPos;

            for (int i = 0; i < iconParts; ++i)
            {
                for (k = 0; k < width[i]; ++k)
                {
                    pos = k / width[i];
                    iconPos = xMax[i] + xDiff[i] * pos - xSub[i];
                    tess.addVertexWithUV(pos, 0, -depth, iconPos, yMax[i]);
                    tess.addVertexWithUV(pos, 0, 0, iconPos, yMax[i]);
                    tess.addVertexWithUV(pos, 1, 0, iconPos, yMin[i]);
                    tess.addVertexWithUV(pos, 1, -depth, iconPos, yMin[i]);
                }
            }

            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(1, 0, 0);
            float posEnd;

            for (int i = 0; i < iconParts; ++i)
            {
                for (k = 0; k < width[i]; ++k)
                {
                    pos = k / width[i];
                    iconPos = xMax[i] + xDiff[i] * pos - xSub[i];
                    posEnd = pos + 1 / width[i];
                    tess.addVertexWithUV(posEnd, 1, -depth, iconPos, yMin[i]);
                    tess.addVertexWithUV(posEnd, 1, 0, iconPos, yMin[i]);
                    tess.addVertexWithUV(posEnd, 0, 0, iconPos, yMax[i]);
                    tess.addVertexWithUV(posEnd, 0, -depth, iconPos, yMax[i]);
                }
            }

            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0, 1, 0);

            for (int i = 0; i < iconParts; ++i)
            {
                for (k = 0; k < height[i]; ++k)
                {
                    pos = k / height[i];
                    iconPos = yMax[i] + yDiff[i] * pos - ySub[i];
                    posEnd = pos + 1 / height[i];
                    tess.addVertexWithUV(0, posEnd, 0, xMax[i], iconPos);
                    tess.addVertexWithUV(1, posEnd, 0, xMin[i], iconPos);
                    tess.addVertexWithUV(1, posEnd, -depth, xMin[i], iconPos);
                    tess.addVertexWithUV(0, posEnd, -depth, xMax[i], iconPos);
                }
            }

            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0, -1, 0);

            for (int i = 0; i < iconParts; ++i)
            {
                for (k = 0; k < height[i]; ++k)
                {
                    pos = k / height[i];
                    iconPos = yMax[i] + yDiff[i] * pos - ySub[i];
                    tess.addVertexWithUV(1, pos, 0, xMin[i], iconPos);
                    tess.addVertexWithUV(0, pos, 0, xMax[i], iconPos);
                    tess.addVertexWithUV(0, pos, -depth, xMax[i], iconPos);
                    tess.addVertexWithUV(1, pos, -depth, xMin[i], iconPos);
                }
            }

            tess.draw();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }

        GL11.glPopMatrix();
    }
}
