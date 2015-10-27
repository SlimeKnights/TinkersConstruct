package tconstruct.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import tconstruct.TConstruct;
import tconstruct.armor.items.TravelWings;
import tconstruct.armor.player.ArmorExtended;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.ArmourGuiSyncPacket;
import tconstruct.world.entity.BlueSlime;

import java.util.Locale;

public class TinkerArmorEvents
{

    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

        if(!event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            return;

        if (TConstruct.random.nextInt(200) == 0 && event.entityLiving instanceof IMob && event.source.damageType.equals("player"))
        {
            ItemStack dropStack = new ItemStack(TinkerArmor.heartCanister, 1, 1);
            EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
            entityitem.delayBeforeCanPickup = 10;
            event.drops.add(entityitem);
        }

        if (event.entityLiving instanceof IBossDisplayData)
        {
            String entityName = event.entityLiving.getClass().getSimpleName().toLowerCase();
            for(String name : PHConstruct.heartDropBlacklist)
                if (name.toLowerCase(Locale.US).equals(entityName))
                    return;

            int count = event.entityLiving instanceof EntityDragon ? 5 : 1;
            for (int i = 0; i < count; i++)
            {
                ItemStack dropStack = new ItemStack(TinkerArmor.heartCanister, 1, 3);
                EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
                entityitem.delayBeforeCanPickup = 10;
                event.drops.add(entityitem);
            }
        }
    }

    /* Abilities */
    @SubscribeEvent
    public void armorMineSpeed (net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event)
    {
        if(event.entityPlayer == null)
            return;

        ItemStack glove = TPlayerStats.get(event.entityPlayer).armor.getStackInSlot(1);
        if(event.entityPlayer.worldObj.isRemote) { // todo: sync extended inventory with clients so this stuff and rendering is done properly...
            if(ArmorProxyClient.armorExtended != null)
                glove = ArmorProxyClient.armorExtended.getStackInSlot(1);
            else
                glove = null;
        }
        if(glove == null || !glove.hasTagCompound())
            return;

        // ok, we got a glove. bonus mining speeeeed
        NBTTagCompound tags = glove.getTagCompound().getCompoundTag(TinkerArmor.travelGlove.getBaseTagName());
        float mineSpeed = tags.getInteger("MiningSpeed");

        float modifier = 1f + mineSpeed / 1000f;
        float base = mineSpeed / 250f;
        event.newSpeed = (event.originalSpeed + base) * modifier;
    }

    @SubscribeEvent
    public void jumpHeight (LivingJumpEvent event)
    {
        ItemStack stack = event.entityLiving.getEquipmentInSlot(2);
        if (stack != null && stack.getItem() instanceof TravelWings)
        {
            event.entityLiving.motionY += 0.2;
        }
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public void perfectDodge(LivingAttackEvent event)
    {
        if(!event.source.isProjectile())
            return;

        // perfect dodge?
        if(!(event.entityLiving instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack chest = player.getCurrentArmor(2);
        if(chest == null || !(chest.getItem() instanceof IModifyable) || !chest.hasTagCompound())
            return;

        NBTTagCompound tags = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
        int dodge = tags.getInteger("Perfect Dodge");
        if(dodge > TConstruct.random.nextInt(10))
            event.setCanceled(true);
    }
    
    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event)
    {
        if (event.entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP)event.entity;
            TPlayerStats stats = TPlayerStats.get(player);
            NBTTagCompound tag = new NBTTagCompound();
            stats.saveNBTData(tag);
            ArmourGuiSyncPacket syncPacket = new ArmourGuiSyncPacket(tag);
            TConstruct.packetPipeline.sendTo(syncPacket, player);
        }
        
    }
}
