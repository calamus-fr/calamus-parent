/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.calamus.common.tools;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author haerwynn
 */
public class Binaries {
	//private static final Log log = LogFactory.getLog(Binaries.class);
	public static final String PARSE_IDS_SEPARATEUR = "-";
	public static final int PARSE_IDS_TAILLE_GROUPE = 25;
	public static final int PARSE_IDS_TAILLE_GROUPE_HEX = 32;

	public static List<Integer> bigBinaryToIntegerList(String bb) {
		//ToolBox.log("bigBinaryToIntegerList " + bb);
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < bb.length(); i++) {
			char c = bb.charAt(bb.length() - i - 1);
			if (c == '1') {
				l.add(i);
			}
		}
		ToolBox.log("  ->" + l);
		return l;
	}
	public static String integerListToBigBinary(List<Integer>ints){
		int max=0;
		for (int i = 0; i < ints.size(); i++) {
			Integer n=ints.get(i);
			if(n!=null){
				max=Math.max(max,n);
			}
		}
		char[] chars = new char[max+1];
		for (int i = 0; i < chars.length; i++) {
			char c;
			if(ints.contains(i)){
				c='1';
			}else c='0';
			chars[chars.length-i-1]=c;
		}
		String r = new String(chars);
		int n = r.indexOf("1");
		if (n == -1) {
			r = "0";
		} else {
			r = r.substring(n);
		}
		ToolBox.log(r);
		return r;
	}

	public static String convertBinaryToHexa(String binaryValue, int length) {
		String s="";
		String h="";
		try {
			int valueI = Integer.parseInt(binaryValue, 2);
			s=Integer.toHexString(valueI);
		} catch (NumberFormatException e) {
			//return Integer.MIN_VALUE;
		}
		while(s.length()<length){
			s="0"+s;
		}
		return s;
	}
	public static int convertBinaryToInt(String binaryValue) {
		int valueI;
		try {
			valueI = Integer.parseInt(binaryValue, 2);
		} catch (NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
		return valueI;
	}
	public static long convertBinaryToLong(String binaryValue) {
		long valueI;
		try {
			valueI = Long.parseLong(binaryValue, 2);
		} catch (NumberFormatException e) {
			System.err.println("e="+e);
			return 0;
		}
		return valueI;
	}
	public static String convertLongToBinary(long value){
		String reBin;
		if (value >= 0) {
			reBin = Long.toBinaryString(value);
		} else {
			reBin = null;
		}
		return reBin;
	}

	public static String convertHexaToBinary(String hex) {
		int n=Integer.decode("#"+hex);
		//int taille=hex.length();
		return Integer.toBinaryString(n);
	}

	public static String bigBinaryToInts(String s) {
		int lng = s.length();
		int nbInts = lng / PARSE_IDS_TAILLE_GROUPE + 1;
		int reste = lng % PARSE_IDS_TAILLE_GROUPE;
		String ints = "";
		for (int i = 0; i < nbInts; i++) {
			String s1 = s.substring(i * PARSE_IDS_TAILLE_GROUPE, i * PARSE_IDS_TAILLE_GROUPE + (i == nbInts - 1 ? reste : PARSE_IDS_TAILLE_GROUPE));
			int n = convertBinaryToInt(s1);
			if (ints.length() > 0) {
				ints += PARSE_IDS_SEPARATEUR;
			}
			ints += n;
		}
		return ints;
	}
	public static String bigBinaryToHexa(String s) {
		System.out.println("bigBinaryToHexa");
		int lng = s.length();
		int nbInts = lng / PARSE_IDS_TAILLE_GROUPE_HEX + 1;
		int reste = lng % PARSE_IDS_TAILLE_GROUPE_HEX;
		String ints = "";
		for (int i = 0; i < nbInts; i++) {
			String s1 = s.substring(i * PARSE_IDS_TAILLE_GROUPE_HEX, i * PARSE_IDS_TAILLE_GROUPE_HEX + (i == nbInts - 1 ? reste : PARSE_IDS_TAILLE_GROUPE_HEX));
			String n = convertBinaryToHexa(s1,PARSE_IDS_TAILLE_GROUPE_HEX/8);
			long n2=convertBinaryToLong(s1);
			System.out.println(s1+"("+n2+") -> "+n);
			ints += n;
		}
		return ints;
	}

	public static String hexaToBigBinary(String grandeChaine) {
		//ToolBox.log("intsToBigBinary " + grandeChaine);
		int taille=PARSE_IDS_TAILLE_GROUPE_HEX/8;
		//String[] ints = grandeChaine.split(PARSE_IDS_SEPARATEUR);
		List<String>strings=new ArrayList<>();
		for(int i=0;i<grandeChaine.length();i+=taille){
			if(i>=grandeChaine.length()-taille){
				strings.add(grandeChaine.substring(i));
			}else{
				strings.add(grandeChaine.substring(i, i+taille));
			}
		}
		String bb = "";
		for (int i = 0; i < strings.size(); i++) {
			//int n = Integer.parseInt(ints[i]);
			String s = convertHexaToBinary(strings.get(i));
			/*while (s.length() < PARSE_IDS_TAILLE_GROUPE) {
				s = "0" + s;
			}*/
			bb = s + bb;
		}
		//ToolBox.log("  ->" + bb);
		return bb;
	}
	public static String intsToBigBinary(String grandeChaine) {
		//ToolBox.log("intsToBigBinary " + grandeChaine);

		String[] ints = grandeChaine.split(PARSE_IDS_SEPARATEUR);
		String bb = "";
		for (int i = 0; i < ints.length; i++) {
			int n = Integer.parseInt(ints[i]);
			String s = convertIntToBinary(n);
			while (s.length() < PARSE_IDS_TAILLE_GROUPE) {
				s = "0" + s;
			}
			bb = s + bb;
		}
		//ToolBox.log("  ->" + bb);
		return bb;
	}

	public static String convertIntToBinary(int value) {
		String reBin;
		if (value >= 0) {
			reBin = Integer.toBinaryString(value);
		} else {
			reBin = null;
		}
		return reBin;
	}

	public static String idInBigIntegerString(String id, String bb){
		String s = id + " in(";
		List<Integer> l = Binaries.bigBinaryToIntegerList(bb);
		s += ListsAndArrays.mergeList(l, ",");
		return s + ")";
	}
	/*public static String idInHexaString(String id, String bb){
		String s = id + " in(";
		List<Integer> l = Binaries.bigBinaryToIntegerList(bb);
		s += ListsAndArrays.mergeList(l, ",");
		return s + ")";
	}*/

	public static boolean selectionNonVide(String ints){
		if (ints == null)
			return false;
		String s = ints.replaceAll(PARSE_IDS_SEPARATEUR, "");
		s = s.replaceAll("0", "");
		return s.length() > 0;
	}

}
