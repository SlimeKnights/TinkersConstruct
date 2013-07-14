package mods.tinker.tconstruct.client.entity;

import mods.tinker.tconstruct.entity.SlimeClone;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SlimeCloneRender extends RenderLiving
{
    private ModelBase scaleAmount;

    public SlimeCloneRender(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3)
    {
        super(par1ModelBase, par3);
        this.scaleAmount = par2ModelBase;
    }

    /**
     * Determines whether Slime Render should pass or not.
     */
    protected int shouldSlimeRenderPass (SlimeClone blueSlime, int par2, float par3)
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
    protected void scaleSlime (SlimeClone slimeClone, float par2)
    {
        float f1 = slimeClone.getSlimeSize();
        float f2 = (slimeClone.sizeHeight + (slimeClone.sizeFactor - slimeClone.sizeHeight) * par2) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GL11.glScalef(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback (EntityLiving par1EntityLiving, float par2)
    {
        this.scaleSlime((SlimeClone) par1EntityLiving, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass (EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.shouldSlimeRenderPass((SlimeClone) par1EntityLiving, par2, par3);
    }

    /*protected ResourceLocation func_98191_a (SlimeClone slime)
    {
        this.loadDownloadableImageTexture(slime.skinUrl, slime.getTexture());
    }

    @Override
    protected void func_98190_a (EntityLiving par1EntityLiving)
    {
        this.func_98191_a((SlimeClone) par1EntityLiving);
    }*/

    @Override
    protected ResourceLocation func_110775_a (Entity entity)
    {
        //return func_98191_a((SlimeClone)entity);
        return null;
    }
}
