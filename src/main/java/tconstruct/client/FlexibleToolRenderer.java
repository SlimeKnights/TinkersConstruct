package tconstruct.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tconstruct.TConstruct;
import tconstruct.library.tools.ToolCore;

public class FlexibleToolRenderer implements IItemRenderer {
    public float depth = 1/32f;

    public void setDepth(float d) { depth = d; }

    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        if(!item.hasTagCompound())
            return false;

        switch (type)
        {
            case ENTITY:
                //GL11.glTranslatef(-0.0625F, -0.0625F, 0F);
                return true;
            case EQUIPPED:
                //GL11.glTranslatef(0.03f, 0F, -0.09375F);
            case EQUIPPED_FIRST_PERSON:
                return true;
            case INVENTORY:
                return true;
            default:
                TConstruct.logger.warn("[TCon] Unhandled render case!");
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

    protected void specialAnimation(ItemRenderType type, ItemStack item) {}

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data) {
        Entity ent = null;
        if (data.length > 1)
            ent = (Entity) data[1];

        IIcon[] parts = new IIcon[toolIcons];
        int iconParts = getIcons(item, type, ent, parts);

        // drawing the inventory is a simple procedure
        if (type == ItemRenderType.INVENTORY) {
            renderInventory(iconParts, parts, item);
            return;
        }

        Tessellator tess = Tessellator.instance;
        float[] xMax = new float[iconParts];
        float[] yMin = new float[iconParts];
        float[] xMin = new float[iconParts];
        float[] yMax = new float[iconParts];

        float[] width = new float[iconParts];
        float[] height = new float[iconParts];
        float[] xDiff = new float[iconParts];
        float[] yDiff = new float[iconParts];
        float[] xSub = new float[iconParts];
        float[] ySub = new float[iconParts];
        for (int i = 0; i < iconParts; ++i)
        {
            IIcon icon = parts[i];
            xMin[i] = icon.getMinU();
            xMax[i] = icon.getMaxU();
            yMin[i] = icon.getMinV();
            yMax[i] = icon.getMaxV();
            width[i] = icon.getIconWidth();
            height[i] = icon.getIconHeight();
            xDiff[i] = xMin[i] - xMax[i];
            yDiff[i] = yMin[i] - yMax[i];
            xSub[i] = 0.5f * (xMax[i] - xMin[i]) / width[i];
            ySub[i] = 0.5f * (yMax[i] - yMin[i]) / height[i];
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if(type != ItemRenderType.ENTITY) {
            GL11.glTranslatef(0.5f, 0.5f, 0);
            GL11.glScalef(0.5f, 0.5f, 0.5f);

            specialAnimation(type, item);
        }

        // prepare colors
        int[] color = new int[iconParts];
        for(int i = 0; i < iconParts; i++)
            color[i] = item.getItem().getColorFromItemStack(item, i);

        // one side
        tess.startDrawingQuads();
        tess.setNormal(0, 0, 1);
        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            tess.addVertexWithUV(-0.5, -0.5, +depth, xMax[i], yMax[i]);
            tess.addVertexWithUV(+0.5, -0.5, +depth, xMin[i], yMax[i]);
            tess.addVertexWithUV(+0.5, +0.5, +depth, xMin[i], yMin[i]);
            tess.addVertexWithUV(-0.5, +0.5, +depth, xMax[i], yMin[i]);
        }
        tess.draw();

        // other side
        tess.startDrawingQuads();
        tess.setNormal(0, 0, -1);
        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            tess.addVertexWithUV(-0.5, +0.5, -depth, xMax[i], yMin[i]);
            tess.addVertexWithUV(+0.5, +0.5, -depth, xMin[i], yMin[i]);
            tess.addVertexWithUV(+0.5, -0.5, -depth, xMin[i], yMax[i]);
            tess.addVertexWithUV(-0.5, -0.5, -depth, xMax[i], yMax[i]);
        }
        tess.draw();

        // make it have "depth"
        tess.startDrawingQuads();
        tess.setNormal(-1, 0, 0);
        float pos;
        float iconPos;

        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            float w = width[i], m = xMax[i], d = xDiff[i], s = xSub[i];
            for (int k = 0, e = (int)w; k < e; ++k)
            {
                pos = k / w;
                iconPos = m + d * pos - s;
                pos -= 0.5f;
                tess.addVertexWithUV(pos, -0.5, -depth, iconPos, yMax[i]);
                tess.addVertexWithUV(pos, -0.5, +depth, iconPos, yMax[i]);
                tess.addVertexWithUV(pos, +0.5, +depth, iconPos, yMin[i]);
                tess.addVertexWithUV(pos, +0.5, -depth, iconPos, yMin[i]);
            }
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(1, 0, 0);
        float posEnd;

        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            float w = width[i], m = xMax[i], d = xDiff[i], s = xSub[i];
            float d2 = 1f / w;
            for (int k = 0, e = (int)w; k < e; ++k)
            {
                pos = k / w;
                iconPos = m + d * pos - s;
                pos -= 0.5f;
                posEnd = pos + d2;
                tess.addVertexWithUV(posEnd, +0.5, -depth, iconPos, yMin[i]);
                tess.addVertexWithUV(posEnd, +0.5, +depth, iconPos, yMin[i]);
                tess.addVertexWithUV(posEnd, -0.5, +depth, iconPos, yMax[i]);
                tess.addVertexWithUV(posEnd, -0.5, -depth, iconPos, yMax[i]);
            }
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);

        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            float h = height[i], m = yMax[i], d = yDiff[i], s = ySub[i];
            float d2 = 1f / h;
            for (int k = 0, e = (int)h; k < e; ++k)
            {
                pos = k / h;
                iconPos = m + d * pos - s;
                pos -= 0.5f;
                posEnd = pos + d2;
                tess.addVertexWithUV(-0.5, posEnd, +depth, xMax[i], iconPos);
                tess.addVertexWithUV(+0.5, posEnd, +depth, xMin[i], iconPos);
                tess.addVertexWithUV(+0.5, posEnd, -depth, xMin[i], iconPos);
                tess.addVertexWithUV(-0.5, posEnd, -depth, xMax[i], iconPos);
            }
        }

        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0, -1, 0);

        for (int i = 0; i < iconParts; ++i)
        {
            tess.setColorOpaque_I(color[i]);
            float h = height[i], m = yMax[i], d = yDiff[i], s = ySub[i];
            for (int k = 0, e = (int)h; k < e; ++k)
            {
                pos = k / h;
                iconPos = m + d * pos - s;
                pos -= 0.5f;
                tess.addVertexWithUV(+0.5, pos, +depth, xMin[i], iconPos);
                tess.addVertexWithUV(-0.5, pos, +depth, xMax[i], iconPos);
                tess.addVertexWithUV(-0.5, pos, -depth, xMax[i], iconPos);
                tess.addVertexWithUV(+0.5, pos, -depth, xMin[i], iconPos);
            }
        }

        tess.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public void renderInventory(int count, IIcon[] icons, ItemStack item)
    {
        Tessellator tess = Tessellator.instance;
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
        GL11.glDisable(GL11.GL_BLEND);

        tess.startDrawingQuads();

        // draw a simple rectangle for the inventory icon
        for (int i = 0; i < count; ++i)
        {
            tess.setColorOpaque_I(item.getItem().getColorFromItemStack(item, i));

            final IIcon icon = icons[i];
            final float xmin = icon.getMinU();
            final float xmax = icon.getMaxU();
            final float ymin = icon.getMinV();
            final float ymax = icon.getMaxV();
            tess.addVertexWithUV( 0, 16, 0, xmin, ymax);
            tess.addVertexWithUV(16, 16, 0, xmax, ymax);
            tess.addVertexWithUV(16,  0, 0, xmax, ymin);
            tess.addVertexWithUV( 0,  0, 0, xmin, ymin);
        }
        tess.draw();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public int getIcons(ItemStack item, ItemRenderType type, Entity ent, IIcon[] parts)
    {
        int iconParts = toolIcons;//tool.getRenderPasses(item.getItemDamage());
        // TODO: have the tools define how many render passes they have
        // (requires more logic rewrite than it sounds like)

        boolean isInventory = type == ItemRenderType.INVENTORY;
        ToolCore tool = (ToolCore) item.getItem();

        IIcon[] tempParts = new IIcon[iconParts];
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
        for (int i = 0; i < iconParts; ++i)
        {
            IIcon part = tempParts[i];
            if (part == null || part == ToolCore.blankSprite || part == ToolCore.emptyIcon)
                ++count;
            else
                parts[i - count] = part;
        }
        iconParts -= count;

        if (iconParts <= 0)
        {
            iconParts = 1;
            parts[0] = ToolCore.blankSprite;
        }

        return iconParts;
    }
}
