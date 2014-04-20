package tconstruct.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.items.armor.TravelGear;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
    Minecraft mc = Minecraft.getMinecraft();

    /* Sounds */

    boolean initSounds;

    @ForgeSubscribe
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
                TConstruct.logger.warning("Failed to register one or more sounds");
            }
        }
    }

    /* Liquids */

    Icon[] stillIcons = new Icon[2];
    Icon[] flowIcons = new Icon[2];

    @ForgeSubscribe
    public void preStitch (TextureStitchEvent.Pre event)
    {
        TextureMap register = event.map;
        if (register.textureType == 0)
        {
            stillIcons[0] = register.registerIcon("tinker:liquid_pigiron");
            flowIcons[0] = register.registerIcon("tinker:liquid_pigiron");
        }
    }

    @ForgeSubscribe
    public void postStitch (TextureStitchEvent.Post event)
    {
        if (event.map.textureType == 0)
        {
            for (int i = 0; i < TContent.fluidBlocks.length; i++)
            {
                TContent.fluids[i].setIcons(TContent.fluidBlocks[i].getIcon(0, 0), TContent.fluidBlocks[i].getIcon(2, 0));
            }
            TContent.pigIronFluid.setIcons(stillIcons[0], flowIcons[0]);
        }
    }
    
    /* Equipables */
    
    @ForgeSubscribe
    public void adjustArmor(RenderPlayerEvent.SetArmorModel event)
    {
        switch(event.slot)
        {
        case 1:
            TProxyClient.wings.onGround = event.renderer.modelBipedMain.onGround;
            TProxyClient.wings.isRiding = event.renderer.modelBipedMain.isRiding;
            TProxyClient.wings.isChild = event.renderer.modelBipedMain.isChild;
            TProxyClient.wings.isSneak = event.renderer.modelBipedMain.isSneak;
            break;
        case 2:
            TProxyClient.glove.onGround = event.renderer.modelBipedMain.onGround;
            TProxyClient.glove.isRiding = event.renderer.modelBipedMain.isRiding;
            TProxyClient.glove.isChild = event.renderer.modelBipedMain.isChild;
            TProxyClient.glove.isSneak = event.renderer.modelBipedMain.isSneak;
            TProxyClient.glove.heldItemLeft = event.renderer.modelBipedMain.heldItemLeft;
            TProxyClient.glove.heldItemRight = event.renderer.modelBipedMain.heldItemRight;
            break;
        case 3:
            TProxyClient.bootbump.onGround = event.renderer.modelBipedMain.onGround;
            TProxyClient.bootbump.isRiding = event.renderer.modelBipedMain.isRiding;
            TProxyClient.bootbump.isChild = event.renderer.modelBipedMain.isChild;
            TProxyClient.bootbump.isSneak = event.renderer.modelBipedMain.isSneak;
            break;
        }
    }

    private static final ResourceLocation hearts = new ResourceLocation("tinker", "textures/gui/newhearts.png");
    private static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    Random rand = new Random();
    int updateCounter = 0;

    boolean tukmc = Loader.isModLoaded("tukmc_Vz");
    GameSettings gs = Minecraft.getMinecraft().gameSettings;

    /* HUD */
    @ForgeSubscribe
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (!tukmc)// Loader check to avoid conflicting with a GUI mod (thanks Vazkii!)
        {
            if (event.type == ElementType.HEALTH)
            {
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

                AttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
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
                int top = scaledHeight - GuiIngameForge.left_height;

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

                int potionOffset = 0;
                PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
                if (potion != null)
                    potionOffset = 18;
                potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
                if (potion != null)
                    potionOffset = 9;

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
                        int y = 0;
                        if (i == regen)
                            y -= 2;
                        this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos + y, 0 + 18 * iter, potionOffset, 9, 9);
                    }
                    if (hp % 2 == 1 && renderHearts < 10)
                    {
                        this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos, 9 + 18 * iter, potionOffset, 9, 9);
                    }
                }

                this.mc.getTextureManager().bindTexture(icons);
                GuiIngameForge.left_height += 10;
                if (absorb > 0)
                    GuiIngameForge.left_height += 10;

                event.setCanceled(true);
            }

            if (event.type == ElementType.CROSSHAIRS && gs.thirdPersonView != 0)
            {
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
