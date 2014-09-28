package tconstruct.world.model;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;
import net.minecraft.util.ResourceLocation;
import tconstruct.world.entity.Crystal;

@SideOnly(Side.CLIENT)
public class CrystalRender extends RenderLiving
{
    /** The creeper model. */
    private ModelBase creeperModel = new CrystalModelSmall();

    public CrystalRender()
    {
        super(new CrystalModelSmall(), 0.5F);
    }

    /**
     * A method used to render a creeper's powered form as a pass model.
     */
    protected int renderCreeperPassModel (Crystal par1EntityCreeper, int par2, float par3)
    {

        return -1;
    }

    protected int func_77061_b (Crystal par1EntityCreeper, int par2, float par3)
    {
        return -1;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before
     * the model is rendered. Args: entityLiving, partialTickTime
     */
    /*
     * protected void preRenderCallback(EntityLiving par1EntityLiving, float
     * par2) { this.updateCreeperScale((Skyla)par1EntityLiving, par2); }
     */

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness,
     * partialTickTime
     */
    protected int getColorMultiplier (EntityLiving par1EntityLiving, float par2, float par3)
    {
        return super.getColorMultiplier(par1EntityLiving, par2, par3);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass (EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.renderCreeperPassModel((Crystal) par1EntityLiving, par2, par3);
    }

    protected int inheritRenderPass (EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.func_77061_b((Crystal) par1EntityLiving, par2, par3);
    }

    @Override
    protected ResourceLocation getEntityTexture (Entity par1Entity)
    {
        return texture;
    }

    static final ResourceLocation texture = new ResourceLocation("assets/tinker/textures/mob/crystalwater.png");
}
