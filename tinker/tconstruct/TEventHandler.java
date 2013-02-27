package tinker.tconstruct;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tinker.tconstruct.crafting.PatternBuilder;

public class TEventHandler
{
	@ForgeSubscribe
	public void registerOre (OreRegisterEvent evt)
	{
		if (evt.Name == "battery")
			TConstruct.content.modE.batteries.add(evt.Ore);

		if (evt.Name == "basicCircuit")
			TConstruct.content.modE.circuits.add(evt.Ore);

		/*if (evt.Name == "ingotCopper")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Copper");
		}

		if (evt.Name == "ingotBronze")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Bronze");
		}*/
	}

	@ForgeSubscribe
	public void bucketFill (FillBucketEvent evt)
	{
		System.out.println("Fired");

		if (evt.current.getItem() == Item.bucketEmpty && evt.target.typeOfHit == EnumMovingObjectType.TILE)
		{
			int hitX = evt.target.blockX;
			int hitY = evt.target.blockY;
			int hitZ = evt.target.blockZ;

			if (!evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current))
            {
				return;
                //return evt.current;
            }
			
			int bID = evt.world.getBlockId(hitX, hitY, hitZ);
			int meta = -1;
			
			//Want switch, can't do it!
			if (bID == TContent.ironStill.blockID)
				meta = 0;
			
			else if (bID == TContent.goldStill.blockID)
				meta = 1;
			
			else if (bID == TContent.copperStill.blockID)
				meta = 2;
			
			else if (bID == TContent.tinStill.blockID)
				meta = 3;
			
			else if (bID == TContent.aluminumStill.blockID)
				meta = 4;
			
			else if (bID == TContent.cobaltStill.blockID)
				meta = 5;
			
			else if (bID == TContent.arditeStill.blockID)
				meta = 6;
			
			else if (bID == TContent.bronzeStill.blockID)
				meta = 7;
			
			else if (bID == TContent.alBrassStill.blockID)
				meta = 8;
			
			else if (bID == TContent.manyullynStill.blockID)
				meta = 9;
			
			else if (bID == TContent.alumiteStill.blockID)
				meta = 10;
			
			else if (bID == TContent.obsidianStill.blockID)
				meta = 11;
			
			else if (bID == TContent.steelStill.blockID)
				meta = 12;
			
			if (meta != -1)
			{
				evt.world.setBlockWithNotify(hitX, hitY, hitZ, 0);
				evt.result = new ItemStack(TContent.buckets, 1, meta);
				evt.setResult(Result.ALLOW);
			}
		}
	}
}
