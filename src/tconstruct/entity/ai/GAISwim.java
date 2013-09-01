package tconstruct.entity.ai;

import tconstruct.entity.GolemBase;

public class GAISwim extends GolemAIBase
{
    public GAISwim(GolemBase golem)
    {
        super(golem);
        this.setMutexBits(4);
        golem.getNavigator().setCanSwim(true);
    }

    public boolean shouldExecute ()
    {
        if (golem.isInWater() || this.golem.handleLavaMovement())
        {
            return true;
        }
        return false;
    }

    /*public void startExecuting()
    {
        if(golem.standby())
        {
            golem.setPatrolMode();
        }
    }*/

    public void updateTask ()
    {
        if (this.golem.getRNG().nextFloat() < 0.8F)
        {
            this.golem.getJumpHelper().setJumping();
        }
    }
}
