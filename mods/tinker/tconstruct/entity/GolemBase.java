package mods.tinker.tconstruct.entity;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;

public class GolemBase extends EntityCreature
{
    public String creator;
    public int maxHealth = 20;
    public int baseAttack;
    public boolean paused;
    float bodyHeight;
    float bodyWidth;
    int movementType;
    public int swings;
    public int targetBlock[];

    public Entity escort;

    public GolemBase(World world)
    {
        super(world);
        //setInitialStats();
    }

    @Override
    public int getMaxHealth ()
    {
        //Workaround for dying on spawn
        if (maxHealth == 0)
            return 20;
        
        return maxHealth;
    }

    @Override
    public void initCreature ()
    {
        //maxHealth = 20;
        baseAttack = 3;
        paused = false;
    }

    /* AI */

    protected void updateWanderPath ()
    {
        if (!paused)
            super.updateWanderPath();
    }

    /* Other */
    protected boolean canDespawn ()
    {
        return false;
    }

    /* Effects */
    public void sparkle ()
    {
        Random random = worldObj.rand;
        double d = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
        double d1 = (float) posY + random.nextFloat() * (float) bodyHeight;
        double d2 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
        double d3 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
        double d4 = (float) posY + random.nextFloat() * (float) bodyHeight;
        double d5 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
        switch (0)
        //(state)
        {
        case 0:
            worldObj.spawnParticle("reddust", d, d1, d2, 0.0D, 1.0D, 0.0D);
            worldObj.spawnParticle("reddust", d3, d4, d5, 0.0D, 1.0D, 0.0D);
            break;

        case 1:
            worldObj.spawnParticle("reddust", d, d1, d2, -1D, 0.0D, 1.0D);
            worldObj.spawnParticle("reddust", d3, d4, d5, -1D, 0.0D, 1.0D);
            break;

        case 2:
            worldObj.spawnParticle("reddust", d, d1, d2, 1.0D, 0.0D, 0.0D);
            worldObj.spawnParticle("reddust", d3, d4, d5, 1.0D, 0.0D, 0.0D);
            break;

        case 3:
            worldObj.spawnParticle("reddust", d, d1, d2, 1.0D, 1.0D, 1.0D);
            worldObj.spawnParticle("reddust", d3, d4, d5, 1.0D, 1.0D, 1.0D);
            break;

        case 4:
            worldObj.spawnParticle("reddust", d, d1, d2, 0.46000000000000002D, 0.28999999999999998D, 0.19D);
            worldObj.spawnParticle("reddust", d3, d4, d5, 0.46000000000000002D, 0.28999999999999998D, 0.19D);
            break;
        }
    }

    public void sparkle (double d, double d1, double d2)
    {
        Random random = worldObj.rand;
        double d3 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
        double d4 = (float) posY + random.nextFloat() * (float) bodyHeight;
        double d5 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
        double d6 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
        double d7 = (float) posY + random.nextFloat() * (float) bodyHeight;
        double d8 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
        worldObj.spawnParticle("reddust", d3, d4, d5, d, d1, d2);
        worldObj.spawnParticle("reddust", d6, d7, d8, d, d1, d2);
    }

    public void shineRadius (float f, double d, double d1, double d2)
    {
        shineRadius(f, d, d1, d2, 2, "reddust");
    }

    public void shineRadius (float f, double d, double d1, double d2, int i, String s)
    {
        d++;
        Random random = new Random();
        float f1 = 0.7F;
        float f2 = bodyHeight / 5;
        for (int j = 1; j <= 2; j++)
        {
            float f3 = f / (float) j;
            for (double d3 = 0.0D; d3 < 1.5707963D; d3 += 0.10000000000000001D)
            {
                float f4 = (float) ((double) f3 * Math.cos(d3));
                float f5 = (float) ((double) f3 * Math.sin(d3));
                for (int k = 0; k < i; k++)
                {
                    worldObj.spawnParticle(s, ((float) posX + f4 + random.nextFloat() * f1) - 0.5F, (float) posY + f2, ((float) posZ + f5 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                }

                for (int l = 0; l < i; l++)
                {
                    worldObj.spawnParticle(s, (((float) posX - f4) + random.nextFloat() * f1) - 0.5F, (float) posY + f2, ((float) posZ + f5 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                }

                for (int i1 = 0; i1 < i; i1++)
                {
                    worldObj.spawnParticle(s, ((float) posX + f4 + random.nextFloat() * f1) - 0.5F, (float) posY + f2, (((float) posZ - f5) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                }

                for (int j1 = 0; j1 < i; j1++)
                {
                    worldObj.spawnParticle(s, (((float) posX - f4) + random.nextFloat() * f1) - 0.5F, (float) posY + f2, (((float) posZ - f5) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                }
            }
        }
    }

    public void shineRadius (float f, double d, double d1, double d2, boolean flag)
    {
        d--;
        Random random = new Random();
        float f1 = 0.7F;
        float f2 = f;
        String s = "reddust";
        float f3 = (float) bodyHeight / 5F;
        for (double d3 = 0.0D; d3 < 1.5707963D; d3 += 0.20000000000000001D)
        {
            float f4 = (float) Math.sin(d3);
            float f5 = (float) Math.cos(d3);
            float f6 = f2 * f4;
            for (double d4 = 0.0D; d4 < 1.5707963D; d4 += 0.20000000000000001D)
            {
                float f7 = (float) Math.sin(d4);
                float f8 = (float) Math.cos(d4);
                float f9 = f2 * f8 * f5;
                float f10 = f2 * f7 * f5;
                worldObj.spawnParticle(s, ((float) posX + f9 + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1,
                        ((float) posZ + f10 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                worldObj.spawnParticle(s, (((float) posX - f9) + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1,
                        ((float) posZ + f10 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                worldObj.spawnParticle(s, ((float) posX + f9 + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1,
                        (((float) posZ - f10) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
                worldObj.spawnParticle(s, (((float) posX - f9) + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1,
                        (((float) posZ - f10) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
            }
        }
    }
    
    public ArrayList findNearbyBlock(int i, int j)
    {
        return findNearbyBlock(i, j, false);
    }

    public ArrayList findNearbyBlock(int i, int j, boolean flag)
    {
        ArrayList arraylist = new ArrayList();
        int k = j;
        int l = (int)posX;
        int i1 = (int)posY;
        int j1 = (int)posZ;
        for (int k1 = 2; k1 >= -1; k1--)
        {
            for (int l1 = 0; l1 <= k; l1 = l1 <= 0 ? Math.abs(l1) + 1 : -l1)
            {
                for (int i2 = 0; i2 <= k; i2 = i2 <= 0 ? Math.abs(i2) + 1 : -i2)
                {
                    int j2 = worldObj.getBlockId(l + l1, i1 + k1, j1 + i2);
                    if (j2 != 0 && j2 == i)
                    {
                        int ai[] = new int[3];
                        ai[0] = l + l1;
                        ai[1] = i1 + k1;
                        ai[2] = j1 + i2;
                        arraylist.add(ai);
                    }
                }
            }
        }

        return arraylist.size() == 0 ? null : arraylist;
    }
    
    public boolean checkNeighbor(int ai[], int i)
    {
        int j = ai[0] + 1;
        int l = ai[1] + 0;
        int j1 = ai[2] + 0;
        if (worldObj.getBlockId(j, l, j1) == i)
        {
            return true;
        }
        j = ai[0] + 0;
        l = ai[1] + 1;
        j1 = ai[2] + 0;
        if (worldObj.getBlockId(j, l, j1) == i)
        {
            return true;
        }
        j = ai[0] + 0;
        l = ai[1] + 0;
        j1 = ai[2] + 1;
        if (worldObj.getBlockId(j, l, j1) == i)
        {
            return true;
        }
        j = ai[0] - 1;
        l = ai[1] + 0;
        j1 = ai[2] + 0;
        if (worldObj.getBlockId(j, l, j1) == i)
        {
            return true;
        }
        j = ai[0] + 0;
        l = ai[1] - 1;
        j1 = ai[2] + 0;
        if (worldObj.getBlockId(j, l, j1) == i)
        {
            return true;
        }
        else
        {
            int k = ai[0] + 0;
            int i1 = ai[1] + 0;
            int k1 = ai[2] - 1;
            return worldObj.getBlockId(k, i1, k1) == i;
        }
    }
}
