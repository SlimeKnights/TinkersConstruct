package mods.tinker.tconstruct.client;

import net.minecraft.block.StepSound;

public class StepSoundSlime extends StepSound
{

    public StepSoundSlime(String par1Str, float par2, float par3)
    {
        super(par1Str, par2, par3);
        // TODO Auto-generated constructor stub
    }

    /**
     * Used when a block breaks, EXA: Player break, Shep eating grass, etc..
     */
    public String getBreakSound()
    {
        return this.stepSoundName + ".big";
    }

    /**
     * Used when a entity walks over, or otherwise interacts with the block.
     */
    public String getStepSound()
    {
        return this.stepSoundName + ".small";
    }

}
