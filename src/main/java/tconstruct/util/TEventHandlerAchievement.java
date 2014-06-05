package tconstruct.util;

import net.minecraftforge.common.util.FakePlayer;
import tconstruct.achievements.TAchievements;
import tconstruct.library.event.SmelteryEvent;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.logic.ToolForgeLogic;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TEventHandlerAchievement
{

    @SubscribeEvent
    public void onToolCrafted (ToolCraftedEvent event)
    {
        if (event.player != null && !(event.player instanceof FakePlayer))
        {
            event.player.addStat(TAchievements.achievements.get("tconstruct:tinkerer"), 1);

            if (event.tool != null && event.tool.getItem() instanceof Weapon)
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct:preparedFight"), 1);
            }

            if (event.inventory != null && event.inventory instanceof ToolForgeLogic && event.tool.getItem() instanceof ToolCore && ((ToolCore) event.tool.getItem()).durabilityTypeExtra() != 0)
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct:proTinkerer"), 1);
            }
        }
    }

    @SubscribeEvent
    public void onItemPlacedIntoCasting (SmelteryEvent.ItemInsertedIntoCasting event)
    {
        if (event.player != null && event.item != null)
        {
            if (event.item.getItem() instanceof ToolCore)
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct:doingItWrong"), 1);
            }
        }
    }
}
