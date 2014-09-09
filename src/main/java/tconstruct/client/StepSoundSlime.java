package tconstruct.client;

import net.minecraft.block.Block.SoundType;

public class StepSoundSlime extends SoundType
{

    public StepSoundSlime(String par1Str, float par2, float par3)
    {
        super(par1Str, par2, par3);
        // TODO Auto-generated constructor stub
    }

    /**
     * Used when a block breaks, EXA: Player break, Shep eating grass, etc..
     */
    @Override
    public String getBreakSound ()
    {
        return this.soundName + ".big";
    }

    /**
     * Used when a entity walks over, or otherwise interacts with the block.
     */
    @Override
    public String getStepResourcePath ()
    {
        return this.soundName + ".small";
    }

}
