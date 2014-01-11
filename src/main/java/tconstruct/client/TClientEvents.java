package tconstruct.client;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.client.armor.WingModel;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.util.player.TPlayerStats;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
    Minecraft mc = Minecraft.getMinecraft();

    /* Sounds */

    boolean initSounds;

    @SubscribeEvent
    public void onSound (SoundLoadEvent event)
    {
        if (!initSounds)
        {
            initSounds = true;
            try
            {
                SoundManager soundmanager = event.manager;
                soundmanager.addSound("tinker:frypan_hit.ogg");
                soundmanager.addSound("tinker:little_saw.ogg");
                soundmanager.addSound("tinker:launcher_clank.ogg");
                TConstruct.logger.info("Successfully loaded sounds.");
            }
            catch (Exception e)
            {
                TConstruct.logger.error("Failed to register one or more sounds");
            }
        }
    }

    /* Liquids */

    IIcon[] stillIcons = new IIcon[2];
    IIcon[] flowIcons = new IIcon[2];

    @SubscribeEvent
    public void preStitch (TextureStitchEvent.Pre event)
    {
        TextureMap register = event.map;
        if (register.textureType == 0)
        {
            stillIcons[0] = register.registerIcon("tinker:liquid_pigiron");
            flowIcons[0] = register.registerIcon("tinker:liquid_pigiron");
        }
    }

    @SubscribeEvent
    public void postStitch (TextureStitchEvent.Post event)
    {
        if (event.map.textureType == 0)
        {
            for (int i = 0; i < TRepo.fluidBlocks.length; i++)
            {
                TRepo.fluids[i].setIcons(TRepo.fluidBlocks[i].getIcon(0, 0), TRepo.fluidBlocks[i].getIcon(2, 0));
            }
            TRepo.pigIronFluid.setIcons(stillIcons[0], flowIcons[0]);
        }
    }

    private static final ResourceLocation hearts = new ResourceLocation("tinker", "textures/gui/newhearts.png");
    private static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    public static int left_height = 39;
    public static int right_height = 39;
    Random rand = new Random();
    int updateCounter = 0;

    /* HUD */
    @SubscribeEvent
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (!Loader.isModLoaded("tukmc_Vz"))// Loader check to avoid conflicting with a GUI mod (thanks Vazkii!)
        {
            if (event.type == ElementType.HEALTH)
            {
                updateCounter++;

                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int scaledWidth = scaledresolution.getScaledWidth();
                int scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 39;
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(mc.thePlayer.getDisplayName());

                boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

                if (mc.thePlayer.hurtResistantTime < 10)
                {
                    highlight = false;
                }

                IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
                int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
                float healthMax = (float) attrMaxHealth.getAttributeValue();
                if (healthMax > 20)
                    healthMax = 20;
                float absorb = this.mc.thePlayer.getAbsorptionAmount();

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
                this.mc.getTextureManager().bindTexture(hearts);

                int hp = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
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

                this.mc.getTextureManager().bindTexture(icons);
                GuiIngameForge.left_height += 10;
                if (absorb > 0)
                    GuiIngameForge.left_height += 10;

                event.setCanceled(true);
            }
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
    
    @SubscribeEvent
    public void fovModifier(FOVUpdateEvent event)
    {
        float f = 1.0F;

        if (event.entity.capabilities.isFlying)
        {
            f *= 1.1F;
        }

        if (event.entity.isUsingItem() && event.entity.getItemInUse().getItem() == Items.bow)
        {
            int i = event.entity.getItemInUseDuration();
            float f1 = (float) i / 20.0F;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 *= f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }
        event.newfov = f;
    }

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
