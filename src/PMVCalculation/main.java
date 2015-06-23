/**
 * Author: Guan-Lin Chao
 * Date: 2015.6.9
 * References: 
 * [1] Hoyt Tyler, Schiavon Stefano, Piccioli Alberto, Moon Dustin, and Steinfeld Kyle, 2013, CBE Thermal Comfort Tool. 
Center for the Built Environment, University of California Berkeley, http://cbe.berkeley.edu/comforttool/
 */

package PMVCalculation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


class PP<X, Y>{
	public final X x;
	public final Y y;
	public PP(X x, Y y){
		this.x = x;
		this.y = y;
	}
	
}



public class main {
	final static double T_lower = 15.0;
	final static double T_upper = 40.0;
	final static double T_interval = 0.5;
	final static int H_lower = 0;
	final static int H_upper = 100;
	final static int H_interval = 5;
	
	final static double T_comfort_lower = 18.0;
	final static double T_comfort_upper = 32.0;
	final static int H_comfort_lower = 50;
	final static int H_comfort_upper = 70;
	
	final static int dimT = (int)((T_upper-T_lower)/T_interval)+1;
	final static int dimH = (H_upper-H_lower)/H_interval + 1;
	
	
	public static void main(String[] args) throws IOException
	{
		
		WritePMVtoFile("sleep","summer");
		WritePMVtoFile("sit","summer");
		WritePMVtoFile("stand","summer");
		WritePMVtoFile("sleep","winter");
		WritePMVtoFile("sit","winter");
		WritePMVtoFile("stand","winter");
		
		/*
		//PMV 2-dim array;
		double[][] PMVSleepSummer=CalPMVArray("sleep","summer");
		double[][] PMVSitSummer=CalPMVArray("sit","summer");
		double[][] PMVStandSummer=CalPMVArray("stand","summer");
		double[][] PMVSleepWinter=CalPMVArray("sleep","winter");
		double[][] PMVSitWinter=CalPMVArray("sit","winter");
		double[][] PMVStandWinter=CalPMVArray("stand","winter");
		//PMV Sort by temperature
		Map<Double,List<PP<Double,Integer>>> SleepSummerbyTemperature=SortPMVArray("temperature",PMVSleepSummer);
		Map<Double,List<PP<Double,Integer>>> SitSummerbyTemperature=SortPMVArray("temperature",PMVSitSummer);
		Map<Double,List<PP<Double,Integer>>> StandSummerbyTemperature=SortPMVArray("temperature",PMVStandSummer);
		Map<Double,List<PP<Double,Integer>>> SleepWinterbyTemperature=SortPMVArray("temperature",PMVSleepWinter);
		Map<Double,List<PP<Double,Integer>>> SitWinterbyTemperature=SortPMVArray("temperature",PMVSitWinter);
		Map<Double,List<PP<Double,Integer>>> StandWinterbyTemperature=SortPMVArray("temperature",PMVStandWinter);
		//PMV Sort by humidity
		Map<Double,List<PP<Double,Integer>>> SleepSummerbyHumidity=SortPMVArray("humidity",PMVSleepSummer);
		Map<Double,List<PP<Double,Integer>>> SitSummerbyHumidity=SortPMVArray("humidity",PMVSitSummer);
		Map<Double,List<PP<Double,Integer>>> StandSummerbyHumidity=SortPMVArray("humidity",PMVStandSummer);
		Map<Double,List<PP<Double,Integer>>> SleepWinterbyHumidity=SortPMVArray("humidity",PMVSleepWinter);
		Map<Double,List<PP<Double,Integer>>> SitWinterbyHumidity=SortPMVArray("humidity",PMVSitWinter);
		Map<Double,List<PP<Double,Integer>>> StandWinterbyHumidity=SortPMVArray("humidity",PMVStandWinter);
		*/
		/*
		double[][] PMVarr=CalPMVArray(args[0],args[1]);
		Map<Double,List<PP<Double,Integer>>> PMVmap = SortPMVArray(args[2],PMVarr);
		if(!PMVmap.containsKey(Double.parseDouble(args[3])))
			System.out.print("PMV Value out of range!");
		else{
			for(int index=0;index < PMVmap.get(Double.parseDouble(args[3])).size();++index)
			{
				System.out.println("("+Double.toString(roundToDec(PMVmap.get(Double.parseDouble(args[3])).get(index).x,1))+","+Integer.toString(PMVmap.get(Double.parseDouble(args[3])).get(index).y)+")");
				}
		}
		*/
		
	}
	
	static double GetPMVFromArray(double temp, int humid, double[][] PMVarray)
	{
		int i = (int)(Math.round(temp*10)-200);
		int j = humid;
		return PMVarray[i][j];
	}
	
	static Map<Double,List<PP<Double,Integer>>> SortPMVArray(String priority, double[][] PMVarray)
	{
		Map<Double,List<PP<Double,Integer>>> PMVmap = ArraytoMap(PMVarray);
		
		Iterator it = PMVmap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			Double key=(Double) pair.getKey();
			List<PP<Double,Integer>> oldList= new ArrayList<PP<Double,Integer>>( (List<PP<Double,Integer>>)pair.getValue() );
			List<PP<Double,Integer>> sortedList;
			if(priority.contentEquals("temperature")){
				sortedList = SortListbyFirstEntry(oldList);
			}
			else if(priority.contentEquals("humidity")){
				sortedList = SortListbySecondEntry(oldList);
			}
			else
			{
				System.out.print("undefined order");
				return null;
			}
			PMVmap.put(key,sortedList);
		}
		it.remove();
		return PMVmap;
	}
	
	static List<PP<Double,Integer>> SortListbyFirstEntry(List<PP<Double,Integer>> list)
	{
		Collections.sort(list, new Comparator<PP<Double,Integer>>()
				{
					public int compare(PP<Double,Integer> p1, PP<Double,Integer> p2)
					{
						if (p1.x > p2.x) return 1;
						else if (p1.x < p2.x) return -1;
						else if (p1.y > p2.y) return 1;
						else if (p1.y < p2.y) return -1;
						else return 0;
					}
				});
		return list;
	}
	
	static List<PP<Double,Integer>> SortListbySecondEntry(List<PP<Double,Integer>> list)
	{
		Collections.sort(list, new Comparator<PP<Double,Integer>>()
				{
					public int compare(PP<Double,Integer> p1, PP<Double,Integer> p2)
					{
						if (p1.y > p2.y) return 1;
						else if (p1.y < p2.y) return -1;
						else if (p1.x > p2.x) return 1;
						else if (p1.x < p2.x) return -1;
						else return 0;
					}
				});
		return list;
	}
	
	static Map<Double,List<PP<Double,Integer>>> ArraytoMap(double[][] array)
	{
		Map<Double,List<PP<Double,Integer>>> map = new HashMap<Double,List<PP<Double,Integer>>>();
		
		for(int rh=0;rh<=100;++rh){
			for(double t=20.0;t<40.1;t+=0.1){
				int i = (int)(Math.round(t*10)-200);
				int j = rh;
				
				double entry1 = array[i][j];
				
				if(map.containsKey(entry1)){
					map.get(entry1).add(new PP(t,j));
				}
				else
				{
					List<PP<Double,Integer>> list=new ArrayList<PP<Double,Integer>>();
					list.add(new PP(t,rh));
					map.put(entry1, list);
				}
			}
		}
		return map;
	}
	
	static double[][] CalPMVArray(String action, String season)
	{
		double[][] PMVarray = new double[dimT][dimH];
		
		double t; // air temperature & mean radiant temperature (°C), from 15.0 to 40.0
		double vel = 0.1; // wind velocity (m/s), from 0.0 to 2.0
		double rh; // relative humidity (%), from 0 to 100
		double met; // metabolic rate (met), sleep-0.7, sit=1.0, stand=1.2
		double clo; // clothing (clo), summer=0.5, winter=1.0
		
		if(action.contentEquals("sleep"))
			met=0.7;
		else if (action.contentEquals("sit"))
			met=1.0;
		else if (action.contentEquals("stand"))
			met=1.2;
		else
		{
			System.out.print("Action undefined");
			return null;
		}
		
		if(season.contentEquals("summer"))
			clo=0.5;
		else if(season.contentEquals("winter"))
			clo=1.0;
		else
		{
			System.out.print("Season undefined");
			return null;
		}
		
		int i = 0; // x index, t
		int j = 0; // y index, rh
		for(rh=0;rh<=100;++rh){
			for(t=20.0;t<40.1;t+=0.1){
				double pmv=calPMV(t,t,vel,rh,met,clo,0.0);
				pmv=roundToDec(pmv,1);
				PMVarray[i][j]=pmv;
				i++;
			}
			j++;
			i=0;
		}
		return PMVarray;
	}
	
	static void WritePMVtoFile(String action, String season) throws IOException // varies with action and season
	{
		double t; // air temperature & mean radiant temperature (°C), from 20.0 to 40.0
		double vel; // wind velocity (m/s), from 0.0 to 2.0
		double rh; // relative humidity (%), from 0 to 100
		double met; // metabolic rate (met), sleep-0.7, sit=1.0, stand=1.2
		double clo; // clothing (clo), summer=0.5, winter=1.0
		
		if(action.contentEquals("sleep"))
			met=0.7;
		else if (action.contentEquals("sit"))
			met=1.0;
		else if (action.contentEquals("stand"))
			met=1.2;
		else
		{
			System.out.print("Action undefined");
			return;
		}
		
		if(season.contentEquals("summer"))
			clo=0.5;
		else if(season.contentEquals("winter"))
			clo=1.0;
		else
		{
			System.out.print("Season undefined");
			return;
		}
		

		for(vel=0.1;vel<0.2;vel+=0.1){
			String filename = new String(action+"_"+season+"_WindVel_"+Double.toString(roundToDec(vel,1)));
			File fout= new File(filename+".txt");
			FileOutputStream fos = new FileOutputStream(fout);
			OutputStreamWriter osw= new OutputStreamWriter(fos);
			osw.write(filename);
			osw.write("\n");
			osw.write("HumidTemp ");
			for (t=T_lower;t<T_upper+T_interval;t+=T_interval)
				osw.write(Double.toString(roundToDec(t,1))+" ");
			osw.write("\n");
			for(rh=H_lower;rh<H_upper+H_interval;rh+=H_interval){
				//System.out.print(Double.toString(rh));
				osw.write(Double.toString(rh)+" ");
				for(t=T_lower;t<T_upper+T_interval;t+=T_interval){
					double pmv=calPMV(t,t,vel,rh,met,clo,0.0);
					pmv=roundToDec(pmv,1);
					
					if (rh>H_comfort_upper) pmv=3.0;
					if (rh<H_comfort_lower) pmv=-3.0;
					if (t>T_comfort_upper) pmv=3.0;
					if (t<T_comfort_lower) pmv=-3.0;
					
					osw.write(pmv+" ");
					//System.out.print(pmv+" ");
				}
				osw.write("\n");
				//System.out.println(" ");
			}
			osw.close();
		}	
			
	}
	
	static double calPMV(double ta, double tr, double vel, double rh, double met, double clo, double wme) {
	    // returns [pmv, ppd]
	    // ta, air temperature (?�°C)
	    // tr, mean radiant temperature (?�°C)
	    // vel, relative air velocity (m/s)
	    // rh, relative humidity (%) Used only this way to input humidity level
	    // met, metabolic rate (met)
	    // clo, clothing (clo)
	    // wme, external work, normally around 0 (met)

	    double pa, icl, m, w, mw, fcl, hcf, taa, tra, tcla, p1, p2, p3, p4,
	    p5, xn, xf, eps, hcn, hc = 1, tcl, hl1, hl2, hl3, hl4, hl5, hl6,
	    ts, pmv, ppd, n;

	    pa = rh * 10 * Math.exp(16.6536 - 4030.183 / (ta + 235));

	    icl = 0.155 * clo; //thermal insulation of the clothing in M2K/W
	    m = met * 58.15; //metabolic rate in W/M2
	    w = wme * 58.15; //external work in W/M2
	    mw = m - w; //internal heat production in the human body
	    if (icl <= 0.078) fcl = 1 + (1.29 * icl);
	    else fcl = 1.05 + (0.645 * icl);

	    //heat transf. coeff. by forced convection
	    hcf = 12.1 * Math.sqrt(vel);
	    taa = ta + 273;
	    tra = tr + 273;
	    tcla = taa + (35.5 - ta) / (3.5 * icl + 0.1);

	    p1 = icl * fcl;
	    p2 = p1 * 3.96;
	    p3 = p1 * 100;
	    p4 = p1 * taa;
	    p5 = 308.7 - 0.028 * mw + p2 * Math.pow(tra / 100, 4);
	    xn = tcla / 100;
	    xf = tcla / 50;
	    eps = 0.00015;

	    n = 0;
	    while (Math.abs(xn - xf) > eps) {
	        xf = (xf + xn) / 2;
	        hcn = 2.38 * Math.pow(Math.abs(100.0 * xf - taa), 0.25);
	        if(hcf > hcn)
	        	hc = hcf;
	        else
	        	hc = hcn;
	        xn = (p5 + p4 * hc - p2 * Math.pow(xf, 4)) / (100 + p3 * hc);
	        ++n;
	        if (n > 150) {
	            System.out.print("Max iterations exceeded");
	            return -1000;
	        }
	    }

	    tcl = 100 * xn - 273;

	    // heat loss diff. through skin 
	    hl1 = 3.05 * 0.001 * (5733 - (6.99 * mw) - pa);
	    // heat loss by sweating
	    if (mw > 58.15) hl2 = 0.42 * (mw - 58.15);
	    else hl2 = 0;
	    // latent respiration heat loss 
	    hl3 = 1.7 * 0.00001 * m * (5867 - pa);
	    // dry respiration heat loss
	    hl4 = 0.0014 * m * (34 - ta);
	    // heat loss by radiation  
	    hl5 = 3.96 * fcl * (Math.pow(xn, 4) - Math.pow(tra / 100, 4));
	    // heat loss by convection
	    hl6 = fcl * hc * (tcl - ta);

	    ts = 0.303 * Math.exp(-0.036 * m) + 0.028;
	    pmv = ts * (mw - hl1 - hl2 - hl3 - hl4 - hl5 - hl6);
	    ppd = 100.0 - 95.0 * Math.exp(-0.03353 * Math.pow(pmv, 4.0) - 0.2179 * Math.pow(pmv, 2.0));

	    return pmv;
	}
	
	static double roundToDec(double num,int dec){
		// dec is how many digits after the point num to be rounded
		return Math.round(num*Math.pow(10, dec))/Math.pow(10, dec);
	}
	
	
}
