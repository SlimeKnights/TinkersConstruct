package tconstruct.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import tconstruct.TConstruct;
import tconstruct.armor.items.TravelWings;
import tconstruct.armor.player.ArmorExtended;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.modifier.IModifyable;
import tconstruct.world.entity.BlueSlime;

public class TinkerArmorEvents
{

    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
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
            if (entityName.contains("entitynpc") || entityName.contains("entitycustomnpc"))
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
        if(event.entityPlayer.worldObj.isRemote) // todo: sync extended inventory with clients so this stuff and rendering is done properly...
            glove = ArmorProxyClient.armorExtended.getStackInSlot(1);
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
}
