package mods.tinker.tconstruct.library;

import java.util.HashMap;

import mods.tinker.tconstruct.skill.Skill;

public class SkillRegistry
{
    public static HashMap<String, Skill> skills = new HashMap<String, Skill>();
    
    static int skillID = 0;
    public static HashMap<Integer, String> skillMapping = new HashMap<Integer, String>(); //Simplifies network transmission
    
    public static void registerSkill(String name, Skill skill)
    {
    	skills.put(name, skill);
    	skillMapping.put(getNextID(), name);
    }

	static Integer getNextID ()
	{
		return skillID++;
	}
}
