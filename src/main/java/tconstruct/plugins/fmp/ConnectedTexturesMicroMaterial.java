package tconstruct.plugins.fmp;

import codechicken.lib.lighting.*;
import codechicken.lib.render.*;
import codechicken.lib.render.uv.MultiIconTransformation;
import codechicken.lib.vec.*;
import codechicken.microblock.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.GlassBlockConnected;
import tconstruct.util.config.PHConstruct;

public class ConnectedTexturesMicroMaterial extends BlockMicroMaterial
{

    public GlassBlockConnected b;

    MultiIconTransformation icont = null;

    public ConnectedTexturesMicroMaterial(GlassBlockConnected block, int meta)
    {
        super(block, meta);
        b = block;
    }
    /*
            @Override
            public void loadIcons ()
            {
                icont = new MultiIconTransformation(b.getIcons());
            }

            @Override
            public void renderMicroFace (Vertex5[] verts, int side, Vector3 pos, LightMatrix lightMatrix, IMicroMaterialRender part)
            {
                renderMicroFace(verts, side, pos, lightMatrix, getColour(part), icont);
            }

            public int determineTextre (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
            {
                if (PHConstruct.connectedTexturesMode == 0)
                {
                    return 0;
                }

                boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

                switch (par5)
                {
                case 0:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 11;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 13;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 14;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 5;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 6;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 8;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 10;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 7;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 9;
                    }
                    else if (isOpenDown)
                    {
                        return 3;
                    }
                    else if (isOpenUp)
                    {
                        return 4;
                    }
                    else if (isOpenLeft)
                    {
                        return 2;
                    }
                    else if (isOpenRight)
                    {
                        return 1;
                    }
                    break;
                case 1:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 11;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 13;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 14;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 5;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 6;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 8;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 10;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 7;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 9;
                    }
                    else if (isOpenDown)
                    {
                        return 3;
                    }
                    else if (isOpenUp)
                    {
                        return 4;
                    }
                    else if (isOpenLeft)
                    {
                        return 2;
                    }
                    else if (isOpenRight)
                    {
                        return 1;
                    }
                    break;
                case 2:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 13;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 14;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 11;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 6;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 5;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 9;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 10;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 7;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 8;
                    }
                    else if (isOpenDown)
                    {
                        return 1;
                    }
                    else if (isOpenUp)
                    {
                        return 2;
                    }
                    else if (isOpenLeft)
                    {
                        return 4;
                    }
                    else if (isOpenRight)
                    {
                        return 3;
                    }
                    break;
                case 3:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 14;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 13;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 11;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 6;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 5;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 10;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 9;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 8;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 7;
                    }
                    else if (isOpenDown)
                    {
                        return 1;
                    }
                    else if (isOpenUp)
                    {
                        return 2;
                    }
                    else if (isOpenLeft)
                    {
                        return 3;
                    }
                    else if (isOpenRight)
                    {
                        return 4;
                    }
                    break;
                case 4:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 14;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 13;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 11;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 6;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 5;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 10;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 9;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 8;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 7;
                    }
                    else if (isOpenDown)
                    {
                        return 1;
                    }
                    else if (isOpenUp)
                    {
                        return 2;
                    }
                    else if (isOpenLeft)
                    {
                        return 3;
                    }
                    else if (isOpenRight)
                    {
                        return 4;
                    }
                    break;
                case 5:
                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
                    {
                        isOpenDown = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
                    {
                        isOpenUp = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
                    {
                        isOpenLeft = true;
                    }

                    if (b.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
                    {
                        isOpenRight = true;
                    }

                    if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 15;
                    }
                    else if (isOpenUp && isOpenDown && isOpenLeft)
                    {
                        return 13;
                    }
                    else if (isOpenUp && isOpenDown && isOpenRight)
                    {
                        return 14;
                    }
                    else if (isOpenUp && isOpenLeft && isOpenRight)
                    {
                        return 11;
                    }
                    else if (isOpenDown && isOpenLeft && isOpenRight)
                    {
                        return 12;
                    }
                    else if (isOpenDown && isOpenUp)
                    {
                        return 6;
                    }
                    else if (isOpenLeft && isOpenRight)
                    {
                        return 5;
                    }
                    else if (isOpenDown && isOpenLeft)
                    {
                        return 9;
                    }
                    else if (isOpenDown && isOpenRight)
                    {
                        return 10;
                    }
                    else if (isOpenUp && isOpenLeft)
                    {
                        return 7;
                    }
                    else if (isOpenUp && isOpenRight)
                    {
                        return 8;
                    }
                    else if (isOpenDown)
                    {
                        return 1;
                    }
                    else if (isOpenUp)
                    {
                        return 2;
                    }
                    else if (isOpenLeft)
                    {
                        return 4;
                    }
                    else if (isOpenRight)
                    {
                        return 3;
                    }
                    break;
                }

                return 0;
            }

            public void renderMicroFace (Vertex5[] verts, int side, Vector3 pos, LightMatrix lightMatrix, int colour, IUVTransformation uvt)
            {
                UV uv = new UV();
                Tessellator t = Tessellator.instance;
                int i = 0;
                while (i < 4)
                {
                    if (CCRenderState.useNormals())
                    {
                        Vector3 n = Rotation.axes[side % 6];
                        t.setNormal((float) n.x, (float) n.y, (float) n.z);
                    }
                    Vertex5 vert = verts[i];
                    if (lightMatrix != null)
                    {
                        LC lc = LC.computeO(vert.vec, side);
                        if (CCRenderState.useModelColours())
                            lightMatrix.setColour(t, lc, colour);
                        lightMatrix.setBrightness(t, lc);
                    }
                    else
                    {
                        if (CCRenderState.useModelColours())
                            CCRenderState.vertexColour(colour);
                    }

                    //			((MultiIconTransformation)uvt).setIconIndex(null, determineTextre(Minecraft.getMinecraft().theWorld, (int)pos.x, (int)pos.y, (int)pos.z, side));

                    uvt.transform(uv.set(vert.uv));
                    t.addVertexWithUV(vert.vec.x + pos.x, vert.vec.y + pos.y, vert.vec.z + pos.z, uv.u, uv.v);
                    i += 1;
                }
            }

            public static void createAndRegister (GlassBlockConnected block)
            {
                MicroMaterialRegistry.registerMaterial(new ConnectedTexturesMicroMaterial(block, 0), block.getUnlocalizedName());
            }*/
}
