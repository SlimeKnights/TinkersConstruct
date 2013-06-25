package mods.tinker.tconstruct.entity.ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mods.tinker.tconstruct.entity.GolemBase;
import mods.touhou_alice_dolls.DollRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;

public class GAIAttackTarget extends GolemAIBase
{
    private PathNavigate pathfinder;
    private EntityLiving theTarget;
    private float speed;
    private int counter;
    private boolean avoidsWater = true;
    //public static String targetEntityRegex;

    public GAIAttackTarget(GolemBase doll)
    {
        super(doll);
        this.speed = doll.getSpeed();
        this.pathfinder = doll.getNavigator();
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute ()
    {
        if (golem.paused)
        {
            return false;
        }
        /*if (golem.isStandbyMode() || golem.isRideonMode())
        {
            return false;
        }
        if (golem.getDollID() != DollRegistry.getDollID("Shanghai"))
        {
            return false;
        }*/
        theTarget = golem.getAttackTarget();
        if (theTarget == null)
        {
            return false;
        }

        String name = EntityList.getEntityString(theTarget);
        if (name == null)
        {
            return false;
        }

        /*Pattern targetPattern = Pattern.compile(targetEntityRegex);
        Matcher targetMatcher = targetPattern.matcher(name);

        if (!targetMatcher.find())
        {
            return false;
        }*/

        return true;
    }

    @Override
    public void startExecuting ()
    {
        counter = 0;
        this.avoidsWater = this.golem.getNavigator().getAvoidsWater();
        this.golem.getNavigator().setAvoidsWater(true);
    }

    @Override
    public boolean continueExecuting ()
    {
       /* if (!golem.isEnable())
        {
            return false;
        }
        if (golem.isStandbyMode() || golem.isRideonMode())
        {
            return false;
        }*/
        if (this.pathfinder.noPath())
        {
            return false;
        }
        if (this.theTarget == null)
        {
            return false;
        }
        if (!this.theTarget.isEntityAlive())
        {
            return false;
        }
        return true;
    }

    @Override
    public void resetTask ()
    {
        this.theTarget = null;
        this.pathfinder.clearPathEntity();
        this.golem.getNavigator().setAvoidsWater(this.avoidsWater);
    }

    @Override
    public void updateTask ()
    {
        if (!this.pathfinder.noPath())
        {
            this.golem.getLookHelper().setLookPositionWithEntity(this.theTarget, 10.0F, (float) this.golem.getVerticalFaceSpeed());
        }

        if (--this.counter <= 0)
        {
            this.counter = 20;

            this.pathfinder.tryMoveToEntityLiving(this.theTarget, this.speed);
            if (this.golem.getDistanceSqToEntity(this.theTarget) < 9f && this.golem.getEntitySenses().canSee(this.theTarget))
            {
                if (this.golem.getHeldItem() != null)
                {
                    this.golem.swingItem();
                }
                //theTarget.attackEntityFrom(DamageSource.causeMobDamage(golem), attackStrength);
                golem.attackEntityAsGolem(theTarget);

                /*if (golem.getOwner() != null)
                {
                    theTarget.attackEntityFrom(DamageSource.causePlayerDamage(golem.getOwner()), attackStrength);
                }
                else
                {*/
                //}
            }
        }
    }
}
