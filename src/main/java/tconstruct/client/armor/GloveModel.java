package tconstruct.client.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

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
}
