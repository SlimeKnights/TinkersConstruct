package biomesoplenty.api;

public class Entities {

	public static Class Mudball = getClass("biomesoplenty.entities.projectiles.EntityMudball");
	public static Class Dart = getClass("biomesoplenty.entities.projectiles.EntityDart");
	public static Class JungleSpider = getClass("biomesoplenty.entities.EntityJungleSpider");
	public static Class Rosester = getClass("biomesoplenty.entities.EntityRosester");
	public static Class Glob = getClass("biomesoplenty.entities.EntityGlob");

	public static Class getClass(String inputstring)
	{
		Class foundclass = null;
		try
		{
			foundclass = Class.forName(inputstring);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return foundclass;
	}
}
