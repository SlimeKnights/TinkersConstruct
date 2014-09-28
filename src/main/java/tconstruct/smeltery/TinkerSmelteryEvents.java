package tconstruct.smeltery;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.smeltery.blocks.*;
import tconstruct.tools.TinkerTools;

public class TinkerSmelteryEvents
{
    @SubscribeEvent
    public void onCrafting (ItemCraftedEvent event)
    {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote)
        {
            if (item == Item.getItemFromBlock(TinkerSmeltery.smeltery) || item == Item.getItemFromBlock(TinkerSmeltery.lavaTank))
            {
                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.smelteryManual)
                {
                    stats.smelteryManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkerTools.manualBook, 1, 2));
                }
            }
        }
    }

    @SubscribeEvent
    public void bucketFill (FillBucketEvent evt)
    {
        if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectType.BLOCK)
        {
            int hitX = evt.target.blockX;
            int hitY = evt.target.blockY;
            int hitZ = evt.target.blockZ;

            if (evt.entityPlayer != null && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current))
            {
                return;
            }

            Block bID = evt.world.getBlock(hitX, hitY, hitZ);
            for (int id = 0; id < TinkerSmeltery.fluidBlocks.length; id++)
            {
                if (bID == TinkerSmeltery.fluidBlocks[id])
                {
                    if (evt.entityPlayer.capabilities.isCreativeMode)
                    {
                        WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                    }
                    else
                    {
                        if (TinkerSmeltery.fluidBlocks[id] instanceof LiquidMetalFinite)
                        {
                            WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                        }
                        else
                        {
                            WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                        }

                        evt.setResult(Result.ALLOW);
                        evt.result = new ItemStack(TinkerSmeltery.buckets, 1, id);
                    }
                }
            }
        }
    }

    // Player interact event - prevent breaking of tank air blocks in creative
    @SubscribeEvent
    public void playerInteract (PlayerInteractEvent event)
    {
        if (event.action == Action.LEFT_CLICK_BLOCK)
        {
            Block block = event.entity.worldObj.getBlock(event.x, event.y, event.z);
            if (block instanceof TankAirBlock)
            {
                event.setCanceled(true);
            }
        }
    }
}
