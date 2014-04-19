package tconstruct.client.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class GloveModel extends ModelBiped
{
    public GloveModel()
    {
        super(0.25f, 0, 64, 32);
        
        this.bipedBody.isHidden = true;
        this.bipedEars.isHidden = true;
        this.bipedHead.isHidden = true;
        this.bipedHeadwear.isHidden = true;
        this.bipedBody.isHidden = true;
        this.bipedRightLeg.isHidden = true;
        this.bipedLeftLeg.isHidden = true;
    }
    
    @Override
    public void setRotationAngles (float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}
