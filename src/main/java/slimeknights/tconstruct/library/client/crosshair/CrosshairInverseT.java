package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class CrosshairInverseT extends Crosshair {

  public CrosshairInverseT(ResourceLocation texture) {
    super(texture);
  }

  public CrosshairInverseT(ResourceLocation texture, int size) {
    super(texture, size);
  }

  @Override
  protected void drawCrosshair(float spread, float width, float height, float partialTicks) {
    drawTipCrosshairPart(width / 2f, height / 2f - spread, 0);
    drawTipCrosshairPart(width / 2f - spread, height / 2f, 1);
    drawTipCrosshairPart(width / 2f + spread, height / 2f, 2);
    drawTipCrosshairPart(width / 2f, height / 2f + spread, 3);
  }

  private void drawTipCrosshairPart(double x, double y, int part) {
    final double s = 8d;
    final double z = -90;

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder vb = tessellator.getBuffer();
    vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX); // 4
    // top part
    if(part == 0) {
      vb.pos(x - s, y - s, z).tex(0, 0).endVertex();
      vb.pos(x, y, z).tex(0.46875, 0.46875).endVertex();
      vb.pos(x + s, y - s, z).tex(0.9375, 0).endVertex();
    }
    // left part
    else if(part == 1) {
      vb.pos(x - s, y - s, z).tex(0, 0).endVertex();
      vb.pos(x - s, y + s, z).tex(0, 0.9375).endVertex();
      vb.pos(x, y, z).tex(0.46875, 0.46875).endVertex();
    }
    // right part
    else if(part == 2) {
      vb.pos(x, y, z).tex(0.46875, 0.46875).endVertex();
      vb.pos(x + s, y + s, z).tex(0.9375, 0.9375).endVertex();
      vb.pos(x + s, y - s, z).tex(0.9375, 0).endVertex();
    }

    tessellator.draw();
  }
}
