package tconstruct.weaponry.client;

import tconstruct.util.Reference;
import tconstruct.library.weaponry.IWindup;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class CrosshairHandler {
    private static Minecraft mc = Minecraft.getMinecraft();

    private static ResourceLocation crossHairSquare = new ResourceLocation(Reference.RESOURCE, "textures/gui/Crosshair.png");
    private static ResourceLocation crossHairTip    = new ResourceLocation(Reference.RESOURCE, "textures/gui/Crosshair2.png");
    private static ResourceLocation crossHairWeird  = new ResourceLocation(Reference.RESOURCE, "textures/gui/Crosshair3.png");
    private static ResourceLocation crossHairSpike  = new ResourceLocation(Reference.RESOURCE, "textures/gui/Crosshair4.png");

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        if(event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
            return;

        ItemStack equipped = mc.thePlayer.getCurrentEquippedItem();
        if(equipped == null || equipped.getItem() == null)
            return;
        if(!(equipped.getItem() instanceof IWindup))
            return;

        IWindup weapon = (IWindup) equipped.getItem();

        float width = event.resolution.getScaledWidth();
        float height = event.resolution.getScaledHeight();

        int type = 0;
        ResourceLocation tex;
        switch (weapon.getCrosshairType())
        {
            case SQUARE: tex = crossHairSquare; break;
            case TIP: tex = crossHairTip; break;
            case WEIRD: tex = crossHairWeird; type = 1; break;
            case SPIKE: tex = crossHairSpike; type = 1; break;
            default: tex = crossHairSquare; type = 0;
        }

        mc.getTextureManager().bindTexture(tex);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);

        float spread = ((1.0f - weapon.getWindupProgress(equipped, mc.thePlayer)) * 25f);

        // 4 square crosshair
        if(type == 0) {
            drawCrosshairPart(width / 2f - spread, height / 2f - spread, 0);
            drawCrosshairPart(width / 2f + spread, height / 2f - spread, 1);
            drawCrosshairPart(width / 2f - spread, height / 2f + spread, 2);
            drawCrosshairPart(width / 2f + spread, height / 2f + spread, 3);
        }
        // 4 triangle crosshair
        else {
            drawAlternateCrosshairPart(width/2f, height/2f - spread, 0);
            drawAlternateCrosshairPart(width/2f - spread, height/2f, 1);
            drawAlternateCrosshairPart(width/2f + spread, height/2f, 2);
            drawAlternateCrosshairPart(width/2f, height/2f + spread, 3);
        }

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glDisable(GL11.GL_BLEND);

        event.setCanceled(true);
    }

    private void drawCrosshairPart(float width, float height, int part)
    {
        double w = (double)width;
        double h = (double)height;

        double s = 4;
        double z = -90;

        double u1 = 0;
        double v1 = 0;

        switch(part)
        {
            // top left
            case 0:
                w -= s;
                h -= s;
                break;
            case 1:
                u1 = 0.5;
                w += s;
                h -= s;
                break;
            case 2:
                v1 = 0.5;
                w -= s;
                h += s;
                break;
            case 3:
                u1 = 0.5;
                v1 = 0.5;
                w += s;
                h += s;
                break;
        }

        double u2 = u1 + 0.5;
        double v2 = v1 + 0.5;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(w - s, h - s, z,  u1, v1);
        tessellator.addVertexWithUV(w - s, h + s, z,  u1, v2);
        tessellator.addVertexWithUV(w + s, h + s, z,  u2, v2);
        tessellator.addVertexWithUV(w + s, h - s, z,  u2, v1);
        tessellator.draw();
    }

    private void drawAlternateCrosshairPart(float width, float height, int part)
    {
        double w = (double)width;
        double h = (double)height;

        final double s = 8d;
        final double z = -90;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES); // 4
        // top part
        if(part == 0) {
            tessellator.addVertexWithUV(w - s, h - s, z, 0, 0);
            tessellator.addVertexWithUV(w, h, z, 0.5, 0.5);
            tessellator.addVertexWithUV(w + s, h - s, z, 1, 0);
        }
        // left part
        else if(part == 1) {
            tessellator.addVertexWithUV(w - s, h - s, z, 0, 0);
            tessellator.addVertexWithUV(w - s, h + s, z, 0, 1);
            tessellator.addVertexWithUV(w, h, z, 0.5, 0.5);
        }
        // right part
        else if(part == 2) {
            tessellator.addVertexWithUV(w, h, z, 0.5, 0.5);
            tessellator.addVertexWithUV(w + s, h + s, z, 1, 1);
            tessellator.addVertexWithUV(w + s, h - s, z, 1, 0);
        }
        // bottom part
        else if(part == 3) {
            tessellator.addVertexWithUV(w, h, z, 0.5, 0.5);
            tessellator.addVertexWithUV(w - s, h + s, z, 0, 1);
            tessellator.addVertexWithUV(w + s, h + s, z, 1, 1);
        }
        tessellator.draw();
    }
}
