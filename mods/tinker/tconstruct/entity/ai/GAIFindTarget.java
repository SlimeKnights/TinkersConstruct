package mods.tinker.tconstruct.entity.ai;

import java.util.List;

import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySheep;

public class GAIFindTarget extends GolemAIBase
{
    private int counter;
    public static double searchRange = 16;
    public static double searchHeight = 4;
    public static String searchEntityRegex;
    public static String targetEntityRegex;

    public GAIFindTarget(GolemBase golem)
    {
        super(golem);
        this.setMutexBits(8);
    }

    public boolean shouldExecute ()
    {
        if (golem.paused)
        {
            return false;
        }
        return true;
    }

    public void startExecuting ()
    {
        counter = 0;
    }

    public boolean continueExecuting ()
    {
        return !golem.paused;
    }

    public void updateTask ()
    {
        if (counter == 0)
        {
            List<EntityLiving> targetList = (List<EntityLiving>) (world.getEntitiesWithinAABB(EntityLiving.class, golem.boundingBox.expand(searchRange, searchHeight, searchRange)));
            /*Pattern searchPattern = Pattern.compile(searchEntityRegex);
            Pattern targetPattern = Pattern.compile(targetEntityRegex);
            Matcher searchMatcher, targetMatcher;
            TreeMap<String, Integer> entityCount = new TreeMap<String, Integer>();*/
            EntityLiving theTarget = null;
            EntityLiving stealTarget = null;

            theTarget = null;
            for (EntityLiving e : targetList)
            {
                /*String name = EntityList.getEntityString(e);
                if (name == null)
                {
                    continue;
                }

                searchMatcher = searchPattern.matcher(name);
                if (searchMatcher.find())
                {
                    if (entityCount.containsKey(name))
                    {
                        int c = entityCount.get(name).intValue() + 1;
                        entityCount.put(name, new Integer(c));
                    }
                    else
                    {
                        entityCount.put(name, new Integer(1));
                    }
                }*/

                //System.out.println("Entity: "+e);
                if (golem.patrolling())
                {
                    //targetMatcher = targetPattern.matcher(name);
                    //if (targetMatcher.find())
                    if (e instanceof IMob)
                    {
                        if (theTarget == null)
                        {
                            theTarget = e;
                        }
                        else
                        {
                            if (golem.getDistanceSqToEntity(theTarget) > golem.getDistanceSqToEntity(e))
                            {
                                theTarget = e;
                            }
                        }
                    }
                }

                if (golem.following())
                {
                    Entity tt = null;
                    if (e instanceof EntityCreature)
                    {
                        tt = ((EntityCreature) e).getEntityToAttack();
                    }
                    else
                    {
                        tt = e.getAttackTarget();
                    }
                    if (golem.isOwner(tt))
                    {
                        if (theTarget == null)
                        {
                            theTarget = e;
                        }
                        else
                        {
                            if (golem.getDistanceSqToEntity(theTarget) > golem.getDistanceSqToEntity(e))
                            {
                                theTarget = e;
                            }
                        }
                    }
                }

                if (golem.patrolling())
                {
                    if (e instanceof EntitySheep)
                    {
                        EntitySheep sheep = (EntitySheep) e;

                        if (!sheep.getSheared() && !sheep.isChild())
                        {
                            if (stealTarget == null)
                            {
                                stealTarget = e;
                            }
                            else
                            {
                                if (golem.getDistanceSqToEntity(stealTarget) > golem.getDistanceSqToEntity(e))
                                {
                                    stealTarget = e;
                                }
                            }
                        }
                    }
                    else if (e instanceof EntityChicken || e instanceof EntityMooshroom || e instanceof EntityIronGolem)
                    {
                        if (stealTarget == null)
                        {
                            stealTarget = e;
                        }
                        else
                        {
                            if (golem.getDistanceSqToEntity(stealTarget) > golem.getDistanceSqToEntity(e))
                            {
                                stealTarget = e;
                            }
                        }
                    }
                }
            }

            /*StringBuffer msg = new StringBuffer(golem.getDollName() + " : ");

            if (entityCount.isEmpty())
            {
                msg.append("No target");
                golem.chatMessage(msg.toString(), 2);
            }
            else
            {
                Iterator it = entityCount.keySet().iterator();
                while (it.hasNext())
                {
                    String s = (String) it.next();
                    int v = entityCount.get(s).intValue();
                    msg.append(s);
                    msg.append("[");
                    msg.append(v);
                    msg.append("] ");
                }
                golem.chatMessage(msg.toString(), 1);
            }*/

            golem.setAttackTarget(theTarget == null ? stealTarget : theTarget);
        }
        counter = (counter + 1) % 20;
    }
}
