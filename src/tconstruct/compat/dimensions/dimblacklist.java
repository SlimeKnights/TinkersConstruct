package tconstruct.compat.dimensions;

import java.util.ArrayList;

import tconstruct.compat.BOP;
import tconstruct.compat.Tforest;
import tconstruct.util.PHConstruct;

public class dimblacklist {
public static ArrayList<Integer> blacklist = new ArrayList<Integer>();
public static ArrayList<Integer> nopool = new ArrayList<Integer>();
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
	if (BOP.pldimid != -100){
		nopool.add(BOP.pldimid);
	}
}
public static boolean isDimInBlacklist(int dim){
	if (dim<0)
	return false;
	if(PHConstruct.slimeIslGenDim0Only && dim != 0){
		return false;
	}
	for (int len = 0;len< blacklist.size(); len++){
		if (blacklist.get(len) == dim)
				return false;
				//System.out.println("[TConstruct]diminblist +" + blacklist.get(len));
	} 
	return true;
	
}
public static boolean isDimNoPool(int dim){
		for (int len = 0;len< nopool.size(); len++){
			if (nopool.get(len) == dim)
				//System.out.println("[TConstruct]DimNoPool "+ nopool.get(len));
					return true;
		} 
		return false;
		
}
}
