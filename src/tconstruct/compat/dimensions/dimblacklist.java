package tconstruct.compat.dimensions;

import java.util.ArrayList;

import tconstruct.compat.Tforest;
import tconstruct.util.PHConstruct;

public class dimblacklist {
public static ArrayList<Integer> blacklist = new ArrayList<Integer>();
public static void getbaddimensions(){
	blacklist.add(1);
	if (Tforest.tfdimid != -100){
		blacklist.add(Tforest.tfdimid);
	}
	if(PHConstruct.cfgDimBlackList.length > 0){
		for (int numdim = 0; numdim< PHConstruct.cfgDimBlackList.length; numdim ++){
			blacklist.add(PHConstruct.cfgDimBlackList[numdim]);
		}
	}
		
}
public static boolean isDimInBlacklist(int dim){
	if (dim<0)
	return false;
	for (int len = 0;len< blacklist.size(); len++){
		if (blacklist.get(len) == dim)
				return false;
	} 
	return true;
	
}
}
