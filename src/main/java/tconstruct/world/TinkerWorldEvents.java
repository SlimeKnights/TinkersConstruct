package tconstruct.world;

import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.BonemealEvent;
import tconstruct.TConstruct;
import tconstruct.tools.TinkerTools;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;

public class TinkerWorldEvents
{
    @SubscribeEvent
    public void onLivingSpawn (LivingSpawnEvent.SpecialSpawn event)
    {
        EntityLivingBase living = event.entityLiving;
        if (living.getClass() == EntitySpider.class && TConstruct.random.nextInt(100) == 0)
        {
            EntityCreeper creeper = new EntityCreeper(living.worldObj);
            spawnEntityLiving(living.posX, living.posY + 1, living.posZ, creeper, living.worldObj);
            if (living.riddenByEntity != null)
                creeper.mountEntity(living.riddenByEntity);
            else
                creeper.mountEntity(living);

            EntityXPOrb orb = new EntityXPOrb(living.worldObj, living.posX, living.posY, living.posZ, TConstruct.random.nextInt(20) + 20);
            orb.mountEntity(creeper);
        }
    }

    public static void spawnEntityLiving (double x, double y, double z, EntityLiving entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }

    /* Bonemeal */
    @SubscribeEvent
    public void bonemealEvent (BonemealEvent event)
    {
        if (!event.world.isRemote)
        {
            if (event.block == TinkerWorld.slimeSapling)
            {
                if (TinkerWorld.slimeSapling.boneFertilize(event.world, event.x, event.y, event.z, event.world.rand, event.entityPlayer))
                    event.setResult(Event.Result.ALLOW);
            }
        }
    }

    /* Damage */
    @SubscribeEvent
    public void onHurt (LivingHurtEvent event)
    {
        EntityLivingBase reciever = event.entityLiving;
        if (reciever instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            // Cutlass
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && player.isUsingItem())
            {
                Item item = stack.getItem();
                if (item == TinkerTools.cutlass)
                {
                    player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 3 * 20, 1));
                }
                else if (item == TinkerTools.battlesign)
                {
                    event.ammount *= 1.5; //Puts battlesign blocking at 3/4 instead of 1/2
                }
            }
        }
        else if (reciever instanceof EntityCreeper)
        {
            Entity attacker = event.source.getEntity();
            if (attacker instanceof EntityLivingBase)
            {
                Entity target = ((EntityCreeper) reciever).getAttackTarget();
                if (target != null)
                {
                    float d1 = reciever.getDistanceToEntity(((EntityCreeper) reciever).getAttackTarget());
                    float d2 = reciever.getDistanceToEntity(attacker);
                    if (d2 < d1)
                    {
                        ((EntityCreeper) event.entityLiving).setAttackTarget((EntityLivingBase) event.source.getEntity());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

        if (event.entityLiving.getClass() == EntityGhast.class)
        {
            if (PHConstruct.uhcGhastDrops)
            {
                for (EntityItem o : event.drops)
                {
                    if (o.getEntityItem().getItem() == Items.ghast_tear)
                    {
                        o.setEntityItemStack(new ItemStack(Items.gold_ingot, 1));
                    }
                }
            }
            else
            {
                ItemHelper.addDrops(event, new ItemStack(Items.ghast_tear, 1));
            }
        }
    }
}
