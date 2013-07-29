package tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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

    /*@ForgeSubscribe
    public void interact (PlayerInteractEvent event)
    {
    	if (event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.isRemote)
    	{
    		System.out.println("Fired");
    		List<Skill> skills = TProxyClient.skillList;
    		if (skills.size() > 0)
    		{
    			Skill walls = TConstruct.playerTracker.getPlayerStats(event.entityPlayer.username).skillList.get(0);
    			walls.rightClickActivate(event.entityPlayer, event.entityPlayer.worldObj);
    		}
    	}
    }*/

    /* Sounds */

    /*boolean initSounds;

    @ForgeSubscribe
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

    /* HUD */
    @ForgeSubscribe
    public void renderHealthbar (RenderGameOverlayEvent.Post event)
    {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int scaledWidth = scaledresolution.getScaledWidth();
        int scaledHeight = scaledresolution.getScaledHeight();
        int xBasePos = scaledWidth / 2 - 91;
        int yBasePos = scaledHeight - 39;
        TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(mc.thePlayer.username);

        PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
        if (potion != null)
            return;
        potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
        if (potion != null)
            return;

        /*if (event.type == RenderGameOverlayEvent.ElementType.HEALTH)
        {
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
        }*/

        /*if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
        	if (stats.skillList == null)
        		return;
        	
            int amount = 0;
            GL11.glScalef(1/16f, 1/16f, 1/16f);
            for (Skill skill : stats.skillList)
            {
            	if (!skill.getActive())
                    GL11.glColor4f(0.5f, 0.5f, 0.5f, 1.0F);
                this.mc.renderEngine.bindTexture(skill.getTextureFile(scaledresolution.getScaleFactor()));
                this.drawTexturedModalRect((2+amount*18)*16, 32, 0, 0, 256, 256);
                amount++;
            }
            GL11.glScalef(16f, 16f, 16f);
        }*/
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
