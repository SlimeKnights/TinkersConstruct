package tconstruct.armor;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import tconstruct.TConstruct;
import tconstruct.armor.items.TravelWings;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;
import tconstruct.world.entity.BlueSlime;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TinkerArmorEvents
{

    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

        if (TConstruct.random.nextInt(200) == 0 && event.entityLiving instanceof IMob && event.source.damageType.equals("player"))
        {
            if (event.entityLiving instanceof BlueSlime)
            {
                BlueSlime slime = (BlueSlime) event.entityLiving;
                if (slime.getSlimeSize() < 8)
                    return;
            }
            int count = event.entityLiving instanceof EntityDragon ? 5 : 1;
            for (int i = 0; i < count; i++)
            {
                ItemStack dropStack = new ItemStack(TinkerArmor.heartCanister, 1, 1);
                EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
                entityitem.delayBeforeCanPickup = 10;
                event.drops.add(entityitem);
            }
        }

        if (event.entityLiving instanceof IBossDisplayData)
        {
            ItemStack dropStack = new ItemStack(TinkerArmor.heartCanister, 1, 3);
            EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
            entityitem.delayBeforeCanPickup = 10;
            event.drops.add(entityitem);
        }
    }

    //Abilities
    boolean morphed;
    boolean morphLoaded = Loader.isModLoaded("Morph");

    @EventHandler
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        System.out.println("Server Tick!");
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);
        if (stats.climbWalls)
        {
            double motionX = player.posX - player.lastTickPosX;
            double motionZ = player.posZ - player.lastTickPosZ;
            double motionY = player.posY - player.lastTickPosY - 0.762;
            if (motionY > 0.0D && (motionX == 0D || motionZ == 0D))
            {
                player.fallDistance = 0.0F;
            }
        }
        //TODO: Proper minimap support
        /*ItemStack stack = player.inventory.getStackInSlot(8);
        if (stack != null && stack.getItem() instanceof ItemMap)
        {
            stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
        }*/
        if (!player.isPlayerSleeping())
        {
            ItemStack chest = player.getCurrentArmor(2);
            if (chest == null || !(chest.getItem() instanceof IModifyable))
            {
                if (!morphLoaded || !morphed)
                    PlayerAbilityHelper.setEntitySize(player, 0.6F, 1.8F);
            }
            else
            {
                NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                int dodge = tag.getInteger("Perfect Dodge");
                if (dodge > 0)
                {
                    if (!morphLoaded || !morphed)
                        PlayerAbilityHelper.setEntitySize(player, Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                }
            }
        }
    }

    /* Abilities */
    @EventHandler
    public void armorMineSpeed (net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event)
    {
        TPlayerStats stats = TPlayerStats.get(event.entityPlayer);
        float modifier = 1f + stats.mineSpeed / 1000f;
        float base = stats.mineSpeed / 250f;
        event.newSpeed = (event.newSpeed + base) * modifier;
    }

    @EventHandler
    public void jumpHeight (LivingJumpEvent event)
    {
        ItemStack stack = event.entityLiving.getEquipmentInSlot(2);
        if (stack != null && stack.getItem() instanceof TravelWings)
        {
            event.entityLiving.motionY += 0.2;
        }
    }

    @EventHandler
    public void slimefall (LivingFallEvent event)
    {
        ItemStack boots = event.entityLiving.getEquipmentInSlot(1);
        if (boots != null && boots.getItem() instanceof IModifyable)
        {
            NBTTagCompound tag = boots.getTagCompound().getCompoundTag(((IModifyable) boots.getItem()).getBaseTagName());
            int sole = tag.getInteger("Slimy Soles");
            if (sole > 0)
            {
                event.distance /= (1 + sole);
                event.entityLiving.fallDistance /= (1 + sole);
            }
        }
    }
}
