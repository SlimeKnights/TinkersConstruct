package tconstruct.entity.ai;

import tconstruct.entity.GolemBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class GolemAIBase extends EntityAIBase
{
    protected GolemBase golem;
    protected World world;

    public GolemAIBase(GolemBase entity)
    {
        golem = entity;
        world = entity.worldObj;
    }

    @Override
    public boolean shouldExecute ()
    {
        return false;
    }
}
