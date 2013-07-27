package tconstruct.client.entity;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelBoxDegrees
{
    /**
     * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
     */
    private PositionTextureVertex[] vertexPositions;

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private TexturedQuad[] quadList;

    /** X vertex coordinate of lower box corner */
    public final float posX1;

    /** Y vertex coordinate of lower box corner */
    public final float posY1;

    /** Z vertex coordinate of lower box corner */
    public final float posZ1;

    /** X vertex coordinate of upper box corner */
    public final float posX2;

    /** Y vertex coordinate of upper box corner */
    public final float posY2;

    /** Z vertex coordinate of upper box corner */
    public final float posZ2;
    public String field_78247_g;

    public ModelBoxDegrees(ModelRendererDegrees par1ModelRenderer, int par2, int par3, float par4, float par5, float par6, int par7, int par8, int par9, float par10)
    {
        this.posX1 = par4;
        this.posY1 = par5;
        this.posZ1 = par6;
        this.posX2 = par4 + (float) par7;
        this.posY2 = par5 + (float) par8;
        this.posZ2 = par6 + (float) par9;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f4 = par4 + (float) par7;
        float f5 = par5 + (float) par8;
        float f6 = par6 + (float) par9;
        par4 -= par10;
        par5 -= par10;
        par6 -= par10;
        f4 += par10;
        f5 += par10;
        f6 += par10;

        if (par1ModelRenderer.mirror)
        {
            float f7 = f4;
            f4 = par4;
            par4 = f7;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(par4, par5, par6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, par5, par6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, par6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(par4, f5, par6, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(par4, par5, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, par5, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(par4, f5, f6, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex;
        this.vertexPositions[1] = positiontexturevertex1;
        this.vertexPositions[2] = positiontexturevertex2;
        this.vertexPositions[3] = positiontexturevertex3;
        this.vertexPositions[4] = positiontexturevertex4;
        this.vertexPositions[5] = positiontexturevertex5;
        this.vertexPositions[6] = positiontexturevertex6;
        this.vertexPositions[7] = positiontexturevertex7;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex5, positiontexturevertex1, positiontexturevertex2, positiontexturevertex6 }, par2 + par9 + par7, par3
                + par9, par2 + par9 + par7 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex4, positiontexturevertex7, positiontexturevertex3 }, par2, par3 + par9, par2
                + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex5, positiontexturevertex4, positiontexturevertex, positiontexturevertex1 }, par2 + par9, par3, par2
                + par9 + par7, par3 + par9, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex2, positiontexturevertex3, positiontexturevertex7, positiontexturevertex6 }, par2 + par9 + par7, par3
                + par9, par2 + par9 + par7 + par7, par3, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex, positiontexturevertex3, positiontexturevertex2 }, par2 + par9, par3 + par9,
                par2 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex5, positiontexturevertex6, positiontexturevertex7 }, par2 + par9 + par7 + par9,
                par3 + par9, par2 + par9 + par7 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);

        if (par1ModelRenderer.mirror)
        {
            for (int j1 = 0; j1 < this.quadList.length; ++j1)
            {
                this.quadList[j1].flipFace();
            }
        }
    }

    /**
     * Draw the six sided box defined by this ModelBox
     */
    @SideOnly(Side.CLIENT)
    public void render (Tessellator par1Tessellator, float par2)
    {
        for (int i = 0; i < this.quadList.length; ++i)
        {
            this.quadList[i].draw(par1Tessellator, par2);
        }
    }

    public ModelBoxDegrees func_78244_a (String par1Str)
    {
        this.field_78247_g = par1Str;
        return this;
    }
}
