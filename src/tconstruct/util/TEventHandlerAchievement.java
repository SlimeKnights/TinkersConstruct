package tconstruct.util;

import tconstruct.achievements.TAchievements;

import net.minecraftforge.common.FakePlayer;

import net.minecraftforge.event.ForgeSubscribe;
import tconstruct.library.event.ToolCraftedEvent;

public class TEventHandlerAchievement {

	@ForgeSubscribe
	public void onToolCrafted(ToolCraftedEvent event){
		if(event.player != null && !(event.player instanceof FakePlayer)){
			event.player.addStat(TAchievements.achievements.get("tconstruct.tinkerer"), 1);
		}
	}
	
}
