package tconstruct.world.model;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tconstruct.world.entity.BlueSlime;

@SideOnly(Side.CLIENT)
public class SlimeRender extends RenderLiving
{
    static final ResourceLocation texture = new ResourceLocation("tinker", "textures/mob/slimeedible.png");
    private ModelBase scaleAmount;

    public SlimeRender(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3)
    {
        super(par1ModelBase, par3);
        this.scaleAmount = par2ModelBase;
    }

    @Override
    public void doRender (EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
        renderBossHealth((BlueSlime) par1EntityLiving);
    }

    public void renderBossHealth (BlueSlime slime)
    {
        if (slime.getSlimeSize() >= 8)
            BossStatus.setBossStatus(slime, true);
    }

    /**
     * Determines whether Slime Render should pass or not.
     */
    protected int shouldSlimeRenderPass (BlueSlime blueSlime, int par2, float par3)
    {
        if (blueSlime.isInvisible())
        {
            return 0;
        }
        else if (par2 == 0)
        {
            this.setRenderPassModel(this.scaleAmount);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        else
        {
            if (par2 == 1)
            {
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            return -1;
        }
    }

    /**
     * sets the scale for the slime based on getSlimeSize in EdibleSlime
     */
    protected void scaleSlime (BlueSlime par1EdibleSlime, float par2)
    {
        float f1 = (float) par1EdibleSlime.getSlimeSize();
        float f2 = (par1EdibleSlime.sizeHeight + (par1EdibleSlime.sizeFactor - par1EdibleSlime.sizeHeight) * par2) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GL11.glScalef(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before
     * the model is rendered. Args: entityLiving, partialTickTime
     */
    @Override
    protected void preRenderCallback (EntityLivingBase par1EntityLiving, float par2)
    {
        this.scaleSlime((BlueSlime) par1EntityLiving, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    @Override
    protected int shouldRenderPass (EntityLivingBase par1EntityLiving, int par2, float par3)
    {
        return this.shouldSlimeRenderPass((BlueSlime) par1EntityLiving, par2, par3);
    }

    @Override
    protected ResourceLocation getEntityTexture (Entity par1Entity)
    {
        return texture;
    }
}
