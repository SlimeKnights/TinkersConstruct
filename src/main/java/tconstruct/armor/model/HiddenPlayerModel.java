package tconstruct.armor.model;

import net.minecraft.client.model.ModelBiped;

public class HiddenPlayerModel extends ModelBiped
{
    public HiddenPlayerModel(float size, int slot)
    {
        super(size, 0, 64, 32);

        if (slot != 0) //Head
        {
            this.bipedHead.isHidden = true;
            this.bipedHeadwear.isHidden = true;
        }
        if (slot != 1 && slot != 5) //Chest, belt
        {
            this.bipedBody.isHidden = true;
        }
        if (slot != 2 && slot != 3) //Legs, shoes
        {
            this.bipedRightLeg.isHidden = true;
            this.bipedLeftLeg.isHidden = true;
        }
        if (slot != 4) //Gloves
        {
            this.bipedRightArm.isHidden = true;
            this.bipedLeftArm.isHidden = true;
        }
        if (slot != 6) //Ears
        {
            this.bipedEars.isHidden = true;
        }
        if (slot != 7) //Cloak
        {
            this.bipedCloak.isHidden = true;
        }
    }
}
