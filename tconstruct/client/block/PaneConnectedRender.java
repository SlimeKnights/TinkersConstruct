package tconstruct.client.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.GlassPaneConnected;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PaneConnectedRender implements ISimpleBlockRenderingHandler
{

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        GlassPaneConnected paneConnected = (GlassPaneConnected) block;
        int l = world.getHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(paneConnected.getMixedBrightnessForBlock(world, x, y, z));
        float f = 1.0F;
        int i1 = paneConnected.colorMultiplier(world, x, y, z);
        float f1 = (float) (i1 >> 16 & 255) / 255.0F;
        float f2 = (float) (i1 >> 8 & 255) / 255.0F;
        float f3 = (float) (i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        Icon icon;
        Icon icon1;

        if (renderer.hasOverrideBlockTexture())
        {
            icon = renderer.overrideBlockTexture;
            icon1 = renderer.overrideBlockTexture;
        }
        else
        {
            int j1 = world.getBlockMetadata(x, y, z);
            icon = renderer.getBlockIconFromSideAndMetadata(paneConnected, 0, j1);
            icon1 = paneConnected.getSideTextureIndex();
        }

        double d0 = (double) icon.getMinU();
        double d1 = (double) icon.getInterpolatedU(8.0D);
        double d2 = (double) icon.getMaxU();
        double d3 = (double) icon.getMinV();
        double d4 = (double) icon.getMaxV();
        double d5 = (double) icon1.getInterpolatedU(7.0D);
        double d6 = (double) icon1.getInterpolatedU(9.0D);
        double d7 = (double) icon1.getMinV();
        double d8 = (double) icon1.getInterpolatedV(8.0D);
        double d9 = (double) icon1.getMaxV();
        double d10 = (double) x;
        double d11 = (double) x + 0.5D;
        double d12 = (double) (x + 1);
        double d13 = (double) z;
        double d14 = (double) z + 0.5D;
        double d15 = (double) (z + 1);
        double d16 = (double) x + 0.5D - 0.0625D;
        double d17 = (double) x + 0.5D + 0.0625D;
        double d18 = (double) z + 0.5D - 0.0625D;
        double d19 = (double) z + 0.5D + 0.0625D;
        boolean flag = paneConnected.canPaneConnectTo(world, x, y, z, NORTH);
        boolean flag1 = paneConnected.canPaneConnectTo(world, x, y, z, SOUTH);
        boolean flag2 = paneConnected.canPaneConnectTo(world, x, y, z, WEST);
        boolean flag3 = paneConnected.canPaneConnectTo(world, x, y, z, EAST);
        boolean flag4 = paneConnected.shouldSideBeRendered(world, x, y + 1, z, 1);
        boolean flag5 = paneConnected.shouldSideBeRendered(world, x, y - 1, z, 0);
        double d20 = 0.01D;
        double d21 = 0.005D;

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                tessellator.addVertexWithUV(d10, (double) (y + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d10, (double) (y + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d10, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d10, (double) (y + 1), d14, d1, d3);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d19, d5, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d19, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d18, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d18, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d19, d6, d7);
                }

                if (flag4 || y < l - 1 && world.isAirBlock(x - 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d8);
                }

                if (flag5 || y > 1 && world.isAirBlock(x - 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d8);
                }
            }
            else if (!flag2 && flag3)
            {
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d12, (double) (y + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d12, (double) (y + 1), d14, d2, d3);
                tessellator.addVertexWithUV(d12, (double) (y + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d12, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d2, d3);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d19, d5, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d19, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 0), d18, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1), d18, d6, d7);
                }

                if (flag4 || y < l - 1 && world.isAirBlock(x + 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d7);
                }

                if (flag5 || y > 1 && world.isAirBlock(x + 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d7);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d10, (double) (y + 1), d14, d0, d3);
            tessellator.addVertexWithUV(d10, (double) (y + 0), d14, d0, d4);
            tessellator.addVertexWithUV(d12, (double) (y + 0), d14, d2, d4);
            tessellator.addVertexWithUV(d12, (double) (y + 1), d14, d2, d3);
            tessellator.addVertexWithUV(d12, (double) (y + 1), d14, d0, d3);
            tessellator.addVertexWithUV(d12, (double) (y + 0), d14, d0, d4);
            tessellator.addVertexWithUV(d10, (double) (y + 0), d14, d2, d4);
            tessellator.addVertexWithUV(d10, (double) (y + 1), d14, d2, d3);

            if (flag4)
            {
                tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d9);
                tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d9);
            }
            else
            {
                if (y < l - 1 && world.isAirBlock(x - 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double) (y + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d8);
                }

                if (y < l - 1 && world.isAirBlock(x + 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) (y + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double) (y + 1) + 0.01D, d18, d5, d7);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d9);
                tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d9);
            }
            else
            {
                if (y > 1 && world.isAirBlock(x - 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double) y - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d8);
                }

                if (y > 1 && world.isAirBlock(x + 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double) y - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double) y - 0.01D, d18, d5, d7);
                }
            }
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                tessellator.addVertexWithUV(d11, (double) (y + 1), d13, d0, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d13, d0, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d13, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d13, d1, d3);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d16, (double) (y + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1), d14, d6, d7);
                    tessellator.addVertexWithUV(d17, (double) (y + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d17, (double) (y + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d16, (double) (y + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d16, (double) (y + 1), d14, d6, d7);
                }

                if (flag4 || y < l - 1 && world.isAirBlock(x, y + 1, z - 1))
                {
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d5, d7);
                }

                if (flag5 || y > 1 && world.isAirBlock(x, y - 1, z - 1))
                {
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d5, d7);
                }
            }
            else if (!flag && flag1)
            {
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d15, d2, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d15, d2, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d15, d1, d3);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d15, d1, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d11, (double) (y + 1), d14, d2, d3);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d17, (double) (y + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d17, (double) (y + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d16, (double) (y + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d16, (double) (y + 1), d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1), d14, d6, d7);
                }

                if (flag4 || y < l - 1 && world.isAirBlock(x, y + 1, z + 1))
                {
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d8);
                }

                if (flag5 || y > 1 && world.isAirBlock(x, y - 1, z + 1))
                {
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d8);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d11, (double) (y + 1), d15, d0, d3);
            tessellator.addVertexWithUV(d11, (double) (y + 0), d15, d0, d4);
            tessellator.addVertexWithUV(d11, (double) (y + 0), d13, d2, d4);
            tessellator.addVertexWithUV(d11, (double) (y + 1), d13, d2, d3);
            tessellator.addVertexWithUV(d11, (double) (y + 1), d13, d0, d3);
            tessellator.addVertexWithUV(d11, (double) (y + 0), d13, d0, d4);
            tessellator.addVertexWithUV(d11, (double) (y + 0), d15, d2, d4);
            tessellator.addVertexWithUV(d11, (double) (y + 1), d15, d2, d3);

            if (flag4)
            {
                tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d9);
                tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d6, d7);
                tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d5, d7);
                tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d9);
                tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d6, d9);
                tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d7);
                tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d7);
                tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d5, d9);
            }
            else
            {
                if (y < l - 1 && world.isAirBlock(x, y + 1, z - 1))
                {
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d5, d7);
                }

                if (y < l - 1 && world.isAirBlock(x, y + 1, z + 1))
                {
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) (y + 1) + 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) (y + 1) + 0.005D, d15, d6, d8);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d9);
                tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d6, d7);
                tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d5, d7);
                tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d9);
                tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d6, d9);
                tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d7);
                tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d7);
                tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d5, d9);
            }
            else
            {
                if (y > 1 && world.isAirBlock(x, y - 1, z - 1))
                {
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d5, d7);
                }

                if (y > 1 && world.isAirBlock(x, y - 1, z + 1))
                {
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double) y - 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double) y - 0.005D, d15, d6, d8);
                }
            }
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory ()
    {
        return false;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

}
