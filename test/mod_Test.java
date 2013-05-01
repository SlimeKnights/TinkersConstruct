package test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/*
 * mDiyo's development testing mod
 * Free everything from dirt!
 */

@Mod(modid = "mod_Test", name = "mod_Test", version = "Test")
public class mod_Test
{
    public static Item xinstick;
    public static Item xinbuilder;
    public static Item TArmorChestplate;
    public static Item negaFood;
    public KeyBinding grabKey;
    EntityPlayer player;

    public static int x;
    public static int y;
    public static int z;
    public static MovingObjectPosition mop;
    Minecraft mc;
    public static boolean leftClick;

    @PreInit
    public void preInit (FMLPreInitializationEvent evt)
    {
    }

    @Init
    public void init (FMLInitializationEvent evt)
    {
        /*TickRegistry.registerTickHandler(new TestTickHandler(), Side.CLIENT);
        TestTickHandler.mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();*/
    }

    @ForgeSubscribe
    public void lastRender (RenderWorldLastEvent event)
    {
        //GuiIngameForge.renderCrosshairs = false;
        if (mc.thePlayer != null)
        {
            ItemStack equipstack = mc.thePlayer.getCurrentEquippedItem();
            if (equipstack != null && equipstack.getItem() == xinbuilder)
            {
                GuiIngameForge.renderCrosshairs = false;
                if (mop != null)
                {
                    double xPos = mop.blockX;
                    double yPos = mop.blockY;
                    double zPos = mop.blockZ;
                    ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);
                    switch (sideHit)
                    {
                    case UP:
                    {
                        yPos += 1;
                        break;
                    }
                    case DOWN:
                    {
                        yPos -= 1;
                        break;
                    }
                    case NORTH:
                    {
                        zPos -= 1;
                        break;
                    }
                    case SOUTH:
                    {
                        zPos += 1;
                        break;
                    }
                    case EAST:
                    {
                        xPos += 1;
                        break;
                    }
                    case WEST:
                    {
                        xPos -= 1;
                        break;
                    }
                    default:
                        break;
                    }

                    Tessellator ts = Tessellator.instance;
                    Tessellator.renderingWorldRenderer = false;
                    //event.context.renderEngine.bindTexture("/terrain.png");
                    int texture = event.context.renderEngine.getTexture("/mods/tinker/textures/blocks/compressed_steel.png");
                    //ts.startDrawing();
                    //System.out.println("Rawr!" +xPos);
                    //GL11.glTranslated(xPos, yPos, zPos);
                    //GL11.glScalef(2, 2, 2);
                    //renderBlockBox(ts);

                    double xD = xPos + 0.5F;
                    double yD = yPos + 0.5F;
                    double zD = zPos + 0.5F;
                    double iPX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
                    double iPY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
                    double iPZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

                    GL11.glDepthMask(false);
                    GL11.glDisable(GL11.GL_CULL_FACE);

                    for (int i = 0; i < 6; i++)
                    {
                        ForgeDirection forgeDir = ForgeDirection.getOrientation(i);
                        int zCorrection = i == 2 ? -1 : 1;
                        GL11.glPushMatrix();
                        GL11.glTranslated(-iPX + xD, -iPY + yD, -iPZ + zD);
                        GL11.glScalef(0.999F, 0.999F, 0.999F);
                        GL11.glRotatef(90, forgeDir.offsetX, forgeDir.offsetY, forgeDir.offsetZ);
                        GL11.glTranslated(0, 0, 0.5f * zCorrection);
                        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                        renderPulsingQuad(texture, 0.75F);
                        GL11.glPopMatrix();
                    }

                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glDepthMask(true);

                    //GL11.glTranslated(iPX, iPY, iPZ);
                    /*event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, xPos, yPos, zPos);
                    event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, -64, 91, 192);*/
                    //event.context.globalRenderBlocks.setRenderBounds(-1, -1, -1, 2, 2, 2);
                    //event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, 0, 0, 0);
                }

            }
            else
            {
                GuiIngameForge.renderCrosshairs = true;
            }
        }
    }

    public static void renderPulsingQuad (int texture, float maxTransparency)
    {

        float pulseTransparency = getPulseValue() * maxTransparency / 3000f;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        Tessellator tessellator = Tessellator.instance;

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, pulseTransparency);

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1, 1, 1, pulseTransparency);

        tessellator.addVertexWithUV(-0.5D, 0.5D, 0F, 0, 1);
        tessellator.addVertexWithUV(0.5D, 0.5D, 0F, 1, 1);
        tessellator.addVertexWithUV(0.5D, -0.5D, 0F, 1, 0);
        tessellator.addVertexWithUV(-0.5D, -0.5D, 0F, 0, 0);

        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private static int getPulseValue ()
    {
        return 2000;

        /*if (doInc)
        {
            pulse += 8;
        }
        else
        {
            pulse -= 8;
        }

        if (pulse == 3000)
        {
            doInc = false;
        }

        if (pulse == 0)
        {
            doInc = true;
        }

        return pulse;*/
    }

    static
    {
        xinstick = new XinStick(10000).setUnlocalizedName("xinstick");
        negaFood = new NegaFood().setUnlocalizedName("negaFood");
        xinbuilder = new XinBuilder(10002).setUnlocalizedName("xinbuilder");
        LanguageRegistry.addName(xinstick, "XinStick");
        LanguageRegistry.addName(negaFood, "Negafood");
        LanguageRegistry.addName(xinbuilder, "XinBuilder");
    }
}
