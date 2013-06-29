package mods.tinker.tconstruct.skill;

import java.util.HashMap;


public class SkillRegistry
{
    public static HashMap<String, Skill> skills = new HashMap<String, Skill>();
    
    static int skillID = 0;
    public static HashMap<Integer, Skill> skillMapping = new HashMap<Integer, Skill>(); //Simplifies network transmission
    
    public static void registerSkill(String name, Skill skill)
    {
    	skills.put(name, skill);
    	skillMapping.put(getNextAvailableID(), skill);
    }

	static Integer getNextAvailableID ()
	{
		return skillID++;
	}
}
