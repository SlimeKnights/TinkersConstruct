package tconstruct.client;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import tconstruct.TConstruct;
import tconstruct.blocks.LiquidMetalFinite;
import tconstruct.client.armor.WingModel;
import tconstruct.common.TContent;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
    Minecraft mc = Minecraft.getMinecraft();

    /* Sounds */

    boolean initSounds;

    /*@ForgeSubscribe
    public void onSound (SoundLoadEvent event)
    {
        if (!initSounds)
        {
            initSounds = true;
            try
            {
                SoundPool soundpool = event.manager.soundPoolSounds;
                soundpool.addSound("tinker:frypan_hit.ogg");
                soundpool.addSound("tinker:little_saw.ogg");
                soundpool.addSound("tinker:launcher_clank.ogg");
                System.out.println("[TConstruct] Successfully loaded sounds.");
            }
            catch (Exception e)
            {
                System.err.println("[TConstruct] Failed to register one or more sounds");
            }
        }
    }*/

    /* Liquids */
    @ForgeSubscribe
    public void postStitch (TextureStitchEvent.Post event)
    {
        for (int i = 0; i < TContent.fluids.length; i++)
        {
            if (TContent.fluidBlocks[i] instanceof LiquidMetalFinite)
            {
                LiquidMetalFinite block = (LiquidMetalFinite) TContent.fluidBlocks[i];
                TContent.fluids[i].setIcons(block.stillIcon, block.flowIcon);
            }
            Fluid lava = FluidRegistry.getFluid("lava");
            lava.setIcons(Block.lavaStill.getIcon(0, 0), Block.lavaStill.getIcon(2, 0));
            Fluid lavawater = FluidRegistry.getFluid("water");
            lava.setIcons(Block.waterStill.getIcon(0, 0), Block.waterStill.getIcon(2, 0));
        }
    }

    private static final ResourceLocation hearts = new ResourceLocation("tinker", "textures/gui/newhearts.png");
    private static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    public static int left_height = 39;
    public static int right_height = 39;
    Random rand = new Random();
    int updateCounter = 0;

    /* HUD */
    @ForgeSubscribe
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (event.type == ElementType.HEALTH)
        {
            event.setCanceled(true);
            updateCounter++;

            ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int scaledWidth = scaledresolution.getScaledWidth();
            int scaledHeight = scaledresolution.getScaledHeight();
            int xBasePos = scaledWidth / 2 - 91;
            int yBasePos = scaledHeight - 39;
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(mc.thePlayer.username);

            boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

            if (mc.thePlayer.hurtResistantTime < 10)
            {
                highlight = false;
            }

            AttributeInstance attrMaxHealth = this.mc.thePlayer.func_110148_a(SharedMonsterAttributes.field_111267_a);
            int health = MathHelper.ceiling_float_int(mc.thePlayer.func_110143_aJ());
            int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
            float healthMax = (float) attrMaxHealth.func_111126_e();
            if (healthMax > 20)
                healthMax = 20;
            float absorb = this.mc.thePlayer.func_110139_bj();

            int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            this.rand.setSeed((long) (updateCounter * 312871));

            int left = scaledWidth / 2 - 91;
            int top = scaledHeight - left_height;

            int regen = -1;
            if (mc.thePlayer.isPotionActive(Potion.regeneration))
            {
                regen = updateCounter % 25;
            }

            final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
            final int BACKGROUND = (highlight ? 25 : 16);
            int MARGIN = 16;
            if (mc.thePlayer.isPotionActive(Potion.poison))
                MARGIN += 36;
            else if (mc.thePlayer.isPotionActive(Potion.wither))
                MARGIN += 72;
            float absorbRemaining = absorb;

            for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
            {
                int b0 = (highlight ? 1 : 0);
                int row = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;

                if (health <= 4)
                    y += rand.nextInt(2);
                if (i == regen)
                    y -= 2;

                drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

                if (highlight)
                {
                    if (i * 2 + 1 < healthLast)
                        drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                    else if (i * 2 + 1 == healthLast)
                        drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
                }

                if (absorbRemaining > 0.0F)
                {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                        drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                    else
                        drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
                else
                {
                    if (i * 2 + 1 < health)
                        drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                    else if (i * 2 + 1 == health)
                        drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
                }
            }

            PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
            if (potion != null)
                return;
            potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
            if (potion != null)
                return;

            //Extra hearts
            this.mc.func_110434_K().func_110577_a(hearts);

            int hp = MathHelper.ceiling_float_int(this.mc.thePlayer.func_110143_aJ());
            for (int iter = 0; iter < hp / 20; iter++)
            {
                int renderHearts = (hp - 20 * (iter + 1)) / 2;
                if (renderHearts > 10)
                    renderHearts = 10;
                for (int i = 0; i < renderHearts; i++)
                {
                    this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos, 0 + 18 * iter, 0, 8, 8);
                }
                if (hp % 2 == 1 && renderHearts < 10)
                {
                    this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos, 9 + 18 * iter, 0, 8, 8);
                }
            }

            this.mc.func_110434_K().func_110577_a(icons);
            GuiIngameForge.left_height += 10;
            if (absorb > 0)
                GuiIngameForge.left_height += 10;
        }
    }

    public void drawTexturedModalRect (int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.draw();
    }

    double zLevel = 0;

    /* Armor */
    ModelBiped model = new ModelBiped(5f);
    WingModel wings = new WingModel();

    /*static
    {
    	model.bipedHead.showModel = false;
    }*/

    private float interpolateRotation (float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

    protected float handleRotationFloat (EntityLiving par1EntityLiving, float par2)
    {
        return (float) par1EntityLiving.ticksExisted + par2;
    }
}
