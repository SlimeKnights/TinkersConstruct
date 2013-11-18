package tconstruct.util;

import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.ForgeSubscribe;
import tconstruct.achievements.TAchievements;
import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.tools.*;

public class TEventHandlerAchievement {

	@ForgeSubscribe
	public void onToolCrafted(ToolCraftedEvent event){
		if(event.player != null && !(event.player instanceof FakePlayer)){
			event.player.addStat(TAchievements.achievements.get("tconstruct.tinkerer"), 1);
			
			if(event.tool != null && event.tool.getItem() instanceof Weapon){
				event.player.addStat(TAchievements.achievements.get("tconstruct.preparedFight"), 1);
			}
			
			if(event.inventory != null && event.inventory instanceof ToolForgeLogic && event.tool.getItem() instanceof ToolCore && ((ToolCore)event.tool.getItem()).durabilityTypeExtra() != 0){
				event.player.addStat(TAchievements.achievements.get("tconstruct.proTinkerer"), 1);
			}
		}
	}
	
}
