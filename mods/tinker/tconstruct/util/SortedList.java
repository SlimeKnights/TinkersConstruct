package mods.tinker.tconstruct.util;

import java.util.ArrayList;
import java.util.Comparator;

/* Sorted List implementation
 * Written by RebelKeithy
 */

public class SortedList<T> extends ArrayList<T>
{
	Comparator comp;
	
	public SortedList(Comparator comp)
	{
		this.comp = comp;
	}
	
	@Override
	public boolean add(T o)
	{
		for(int n = 0; n < this.size(); n++)
		{
			if(comp.compare(this.get(n), o) >= 0)
			{
				this.add(n+1, o);
				return true;
			}
		}
		
		this.add(this.size(), o);
		return true;
	}
}