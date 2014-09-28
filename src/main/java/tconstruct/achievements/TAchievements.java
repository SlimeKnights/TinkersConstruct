package tconstruct.achievements;

import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

public class TAchievements
{

    private static AchievementPage achievementsPage;
    private static HashMap<String, Achievement> achievementsList = new HashMap<String, Achievement>();

    /**
     * Adds an achievement and registers it, so there is no need to call .registerStat
     * @param name The name of the achievement
     * @param achievement The achievement
     */
    public static void addAchievement (String name, Achievement achievement)
    {
        if (!PHConstruct.achievementsEnabled)
        {
            return;
        }

        achievementsList.put(name, achievement.registerStat());
    }

    /**
     * Returns a registered achievement
     * @param name The name of the achievement
     * @return The achievement
     */
    public static Achievement getAchievement (String name)
    {
        return achievementsList.get(name);
    }

    /**
     * Grants the achievement
     * @param player The player that earned the achievement
     * @param name The name of the achievement
     */
    public static void triggerAchievement (EntityPlayer player, String name)
    {
        if (!PHConstruct.achievementsEnabled)
        {
            return;
        }

        Achievement ach = getAchievement(name);

        if (ach != null)
        {
            player.triggerAchievement(ach);
        }
    }

    /**
     * Adds all the achievements included in TConstruct, call before registerAchievementPane is called
     */
    public static void addDefaultAchievements ()
    {
        if (!PHConstruct.achievementsEnabled)
        {
            return;
        }

        addAchievement("tconstruct.beginner", new Achievement("tconstruct.beginner", "tconstruct.beginner", 0, 0, TinkerTools.manualBook, null).initIndependentStat());
        addAchievement("tconstruct.pattern", new Achievement("tconstruct.pattern", "tconstruct.pattern", 2, 1, TinkerTools.blankPattern, getAchievement("tconstruct.beginner")));
        addAchievement("tconstruct.tinkerer", new Achievement("tconstruct.tinkerer", "tconstruct.tinkerer", 2, 2, new ItemStack(TinkerTools.titleIcon, 1, 4096), getAchievement("tconstruct.pattern")));
        addAchievement("tconstruct.preparedFight", new Achievement("tconstruct.preparedFight", "tconstruct.preparedFight", 1, 3, new ItemStack(TinkerTools.titleIcon, 1, 4097), getAchievement("tconstruct.tinkerer")));
        addAchievement("tconstruct.proTinkerer", new Achievement("tconstruct.proTinkerer", "tconstruct.proTinkerer", 4, 3, new ItemStack(TinkerTools.titleIcon, 1, 4098), getAchievement("tconstruct.tinkerer")));
        addAchievement("tconstruct.smelteryMaker", new Achievement("tconstruct.smelteryMaker", "tconstruct.smelteryMaker", -2, -1, TinkerSmeltery.smeltery, getAchievement("tconstruct.beginner")));
        addAchievement("tconstruct.enemySlayer", new Achievement("tconstruct.enemySlayer", "tconstruct.enemySlayer", 0, 5, new ItemStack(TinkerTools.titleIcon, 1, 4099), getAchievement("tconstruct.preparedFight")));
        addAchievement("tconstruct.dualConvenience", new Achievement("tconstruct.dualConvenience", "tconstruct.dualConvenience", 0, 7, new ItemStack(TinkerTools.titleIcon, 1, 4100), getAchievement("tconstruct.enemySlayer")).setSpecial());
    }

    /**
     * Should be called after all the achievements are loaded (PostInit would be good)
     */
    public static void registerAchievementPane ()
    {
        if (!PHConstruct.achievementsEnabled)
        {
            return;
        }

        Achievement[] achievements = new Achievement[achievementsList.size()];

        achievements = achievementsList.values().toArray(achievements);
        achievementsPage = new AchievementPage(StatCollector.translateToLocal("tconstruct.achievementPage.name"), achievements);
        AchievementPage.registerAchievementPage(achievementsPage);
    }

}
