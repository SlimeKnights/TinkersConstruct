package mods.tinker.tconstruct.client.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CloneHeadModel extends ModelBase
{
    /** The slime's bodies, both the inside box and the outside box */
    ModelRenderer slimeBodies;
    ModelRenderer slimeHeadwear;

    public CloneHeadModel(float scale)
    {
        this.slimeBodies = new ModelRenderer(this, 0, 0);
        this.slimeBodies.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
        this.slimeHeadwear = new ModelRenderer(this, 32, 0);
        this.slimeHeadwear.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8, scale);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render (Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.slimeBodies.render(par7);
        this.slimeHeadwear.render(par7);
    }
}
