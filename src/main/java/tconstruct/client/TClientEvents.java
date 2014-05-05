package tconstruct.client;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.FIRST_PERSON_MAP;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import tconstruct.TConstruct;
import tconstruct.client.armor.WingModel;
import tconstruct.common.TRepo;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
    Minecraft mc = Minecraft.getMinecraft();
    int scaledWidth;
    int scaledHeight;
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
                /*
                 * SoundManager soundmanager = event.manager;
                 * soundmanager.addSound("tinker:frypan_hit.ogg");
                 * soundmanager.addSound("tinker:little_saw.ogg");
                 * soundmanager.addSound("tinker:launcher_clank.ogg");
                 * TConstruct.logger.info("Successfully loaded sounds.");
                 */
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
        if (register.getTextureType() == 0)
        {
            stillIcons[0] = register.registerIcon("tinker:liquid_pigiron");
            flowIcons[0] = register.registerIcon("tinker:liquid_pigiron");
        }
    }

    @SubscribeEvent
    public void postStitch (TextureStitchEvent.Post event)
    {
        if (event.map.getTextureType() == 0)
        {
            for (int i = 0; i < TRepo.fluidBlocks.length; i++)
            {
                TRepo.fluids[i].setIcons(TRepo.fluidBlocks[i].getIcon(0, 0), TRepo.fluidBlocks[i].getIcon(2, 0));
            }
            TRepo.pigIronFluid.setIcons(stillIcons[0], flowIcons[0]);
        }
    }

    /* Equipables */

    @SubscribeEvent
    public void adjustArmor (RenderPlayerEvent.SetArmorModel event)
    {
        switch (event.slot)
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
    // public static int left_height = 39;
    // public static int right_height = 39;
    Random rand = new Random();
    int updateCounter = 0;

    GameSettings gs = Minecraft.getMinecraft().gameSettings;

    /* HUD */
    @SubscribeEvent
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (!Loader.isModLoaded("tukmc_Vz"))// Loader check to avoid conflicting
                                            // with a GUI mod (thanks Vazkii!)
        {
            if (event.type == ElementType.HEALTH)
            {
                updateCounter++;

                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                scaledWidth = scaledresolution.getScaledWidth();
                scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 39;

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
                            drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); // 6
                        else if (i * 2 + 1 == healthLast)
                            drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); // 7
                    }

                    if (absorbRemaining > 0.0F)
                    {
                        if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                            drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); // 17
                        else
                            drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
                        absorbRemaining -= 2.0F;
                    }
                    else
                    {
                        if (i * 2 + 1 < health)
                            drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 4
                        else if (i * 2 + 1 == health)
                            drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); // 5
                    }
                }
                int potionOffset = 0;
                PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
                if (potion != null)
                    potionOffset = 18;
                potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
                if (potion != null)
                    potionOffset = 9;
                // Extra hearts
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
        }
    }

    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

    @SubscribeEvent
    public void renderMinimap (RenderGameOverlayEvent.Post event)
    {
        if (event.type == ElementType.ALL)
        {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(8);
            if (stack != null && stack.getItem() instanceof ItemMap)
            {
                //stack.getItem().onUpdate(stack, mc.thePlayer.worldObj, mc.thePlayer, 8, true);
                float scale = 2 / 3F;
                float position = 8f;
                GL11.glTranslatef(position, position, 0f);
                GL11.glScalef(scale, scale, scale);
                this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                byte b0 = 7;
                tessellator.addVertexWithUV((double) (0 - b0), (double) (128 + b0), 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV((double) (128 + b0), (double) (128 + b0), 0.0D, 1.0D, 1.0D);
                tessellator.addVertexWithUV((double) (128 + b0), (double) (0 - b0), 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV((double) (0 - b0), (double) (0 - b0), 0.0D, 0.0D, 0.0D);
                tessellator.draw();

                GL11.glTranslatef(0f, 0F, 100f);
                IItemRenderer custom = MinecraftForgeClient.getItemRenderer(stack, FIRST_PERSON_MAP);
                MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, this.mc.theWorld);

                if (custom == null)
                {
                    if (mapdata != null)
                    {
                        RenderManager.instance.itemRenderer.mapItemRenderer.renderMap(this.mc.thePlayer, this.mc.getTextureManager(), mapdata);
                    }
                }
                else
                {
                    custom.renderItem(FIRST_PERSON_MAP, stack, mc.thePlayer, mc.getTextureManager(), mapdata);
                }
                GL11.glTranslatef(0f, 0F, 100f);
                renderMap(mc.thePlayer, mc.renderEngine, mapdata);
                scale = 3.0F / 2.0F;
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(-position, -position, 0f);
                /*
                // Map itself

                MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, this.mc.theWorld);
                if (mapdata != null)
                {
                    RenderManager.instance.itemRenderer.renderMap(this.mc.thePlayer, this.mc.getTextureManager(), mapdata);
                }*/

            }

        }
    }

    public void renderMap (EntityPlayer par1EntityPlayer, TextureManager par2TextureManager, MapData par3MapData)
    {
        Tessellator tessellator = Tessellator.instance;

        int k1 = 0;
        int b1 = 0;
        int b2 = 0;
        for (Iterator iterator = par3MapData.playersVisibleOnMap.values().iterator(); iterator.hasNext(); ++k1)
        {
            MapCoord mapcoord = (MapCoord) iterator.next();
            GL11.glPushMatrix();
            GL11.glTranslatef((float) b1 + (float) mapcoord.centerX / 2.0F + 64.0F, (float) b2 + (float) mapcoord.centerZ / 2.0F + 64.0F, -0.02F);
            GL11.glRotatef((float) (mapcoord.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(4.0F, 4.0F, 3.0F);
            GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
            float f1 = (float) (mapcoord.iconSize % 4 + 0) / 4.0F;
            float f2 = (float) (mapcoord.iconSize / 4 + 0) / 4.0F;
            float f3 = (float) (mapcoord.iconSize % 4 + 1) / 4.0F;
            float f4 = (float) (mapcoord.iconSize / 4 + 1) / 4.0F;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-1.0D, 1.0D, (double) ((float) k1 * 0.001F), (double) f1, (double) f2);
            tessellator.addVertexWithUV(1.0D, 1.0D, (double) ((float) k1 * 0.001F), (double) f3, (double) f2);
            tessellator.addVertexWithUV(1.0D, -1.0D, (double) ((float) k1 * 0.001F), (double) f3, (double) f4);
            tessellator.addVertexWithUV(-1.0D, -1.0D, (double) ((float) k1 * 0.001F), (double) f1, (double) f4);
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -0.04F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
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

    /*
     * @SubscribeEvent public void fovModifier(FOVUpdateEvent event) { float f =
     * 1.0F;
     * 
     * if (event.entity.capabilities.isFlying) { f *= 1.1F; }
     * 
     * if (event.entity.isUsingItem() && event.entity.getItemInUse().getItem()
     * == Items.bow) { int i = event.entity.getItemInUseDuration(); float f1 =
     * (float) i / 20.0F;
     * 
     * if (f1 > 1.0F) { f1 = 1.0F; } else { f1 *= f1; }
     * 
     * f *= 1.0F - f1 * 0.15F; } event.newfov = f; }
     */

    /* Armor */
    ModelBiped model = new ModelBiped(5f);
    WingModel wings = new WingModel();

    /*
     * static { model.bipedHead.showModel = false; }
     */

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
