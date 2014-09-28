package tconstruct.armor.model;

import net.minecraft.client.model.*;

public class BootBump extends ModelBiped
{
    ModelRenderer rightBump;
    ModelRenderer leftBump;

    public BootBump()
    {
        super(0.5f, 0, 64, 32);

        rightBump = new ModelRenderer(this, 24, 0);
        rightBump.addBox(-0.5F, -1F, 0F, 4, 3, 1, 0.5f);
        rightBump.setRotationPoint(-1.5F, 10F, -3.75F);
        bipedRightLeg.addChild(rightBump);
        leftBump = new ModelRenderer(this, 24, 0);
        leftBump.addBox(-0.5F, -1F, 0F, 4, 3, 1, 0.5f);
        leftBump.setRotationPoint(-1.5F, 10F, -3.75F);
        bipedLeftLeg.addChild(leftBump);

        this.bipedCloak.isHidden = true;
        this.bipedEars.isHidden = true;
        this.bipedHead.isHidden = true;
        this.bipedHeadwear.isHidden = true;
        this.bipedBody.isHidden = true;
        this.bipedRightArm.isHidden = true;
        this.bipedLeftArm.isHidden = true;
    }
}
