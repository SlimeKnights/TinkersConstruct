package tconstruct.achievements;

import java.util.HashMap;

import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

public class TAchievements
{

    // Add achievements in here before the load executes(end of TC's init event)
    public static HashMap<String, Achievement> achievements = new HashMap<String, Achievement>();

    // Warning: Will be null until after the init stage
    public static AchievementPage achievementPage = null;

    public static void init ()
    {
        Achievement[] achs = new Achievement[achievements.values().size()];

        for (int i = 0; i < achs.length; i++)
        {
            achs[i] = (Achievement) achievements.values().toArray()[i];
        }

        achievementPage = new AchievementPage(StatCollector.translateToLocal("tconstruct.achievementPage.name"), achs);

        AchievementPage.registerAchievementPage(achievementPage);
    }

}
