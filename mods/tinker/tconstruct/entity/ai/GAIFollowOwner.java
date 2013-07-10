package mods.tinker.tconstruct.entity.ai;

import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;

public class GAIFollowOwner extends GolemAIBase
{
    private EntityLiving leader;
    private float speed;
    private float maxDist;
    private float minDist;
    private PathNavigate pathfinder;
    private boolean avoidsWater;
    private int counter;

    public GAIFollowOwner(GolemBase golem)
    {
        super(golem);
        this.speed = golem.getSpeed();
        this.minDist = 4.0f;
        this.maxDist = 2.5f;
        this.pathfinder = golem.getNavigator();
        this.setMutexBits(3);
    }

    public boolean shouldExecute ()
    {
        EntityLiving owner = this.golem.getOwner();

        if (owner == null)
        {
            return false;
        }
        if (this.golem.getDistanceSqToEntity(owner) < (double) (this.minDist * this.minDist))
        {
            return false;
        }

        this.leader = owner;
        return true;
    }

    public boolean continueExecuting ()
    {
        if (this.pathfinder.noPath())
        {
            return false;
        }
        if (this.golem.getDistanceSqToEntity(this.leader) < (double) (this.maxDist * this.maxDist))
        {
            return false;
        }
        return true;
    }

    public void startExecuting ()
    {
        this.counter = 0;
        this.avoidsWater = this.golem.getNavigator().getAvoidsWater();
        this.golem.getNavigator().setAvoidsWater(false);
    }

    public void resetTask ()
    {
        this.leader = null;
        this.pathfinder.clearPathEntity();
        this.golem.getNavigator().setAvoidsWater(this.avoidsWater);
    }

    public void updateTask ()
    {
        this.golem.getLookHelper().setLookPositionWithEntity(this.leader, 10.0F, (float) this.golem.getVerticalFaceSpeed());

        if (--this.counter <= 0)
        {
            this.counter = 10;

            if (!this.pathfinder.tryMoveToEntityLiving(this.leader, this.speed))
            {
                if (this.golem.getDistanceSqToEntity(this.leader) >= 144.0D)
                {
                    int var1 = MathHelper.floor_double(this.leader.posX) - 2;
                    int var2 = MathHelper.floor_double(this.leader.posZ) - 2;
                    int var3 = MathHelper.floor_double(this.leader.boundingBox.minY);

                    for (int var4 = 0; var4 <= 4; ++var4)
                    {
                        for (int var5 = 0; var5 <= 4; ++var5)
                        {
                            if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && this.world.doesBlockHaveSolidTopSurface(var1 + var4, var3 - 1, var2 + var5)
                                    && !this.world.isBlockNormalCube(var1 + var4, var3, var2 + var5) && !this.world.isBlockNormalCube(var1 + var4, var3 + 1, var2 + var5))
                            {
                                this.golem.setLocationAndAngles((double) ((float) (var1 + var4) + 0.5F), (double) var3, (double) ((float) (var2 + var5) + 0.5F), this.golem.rotationYaw,
                                        this.golem.rotationPitch);
                                this.pathfinder.clearPathEntity();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
