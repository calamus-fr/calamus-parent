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
public class ListsAndArrays {

	//private static final Log log = LogFactory.getLog(ListsAndArrays.class);
	public static String[] newArrayOfStrings(List<String> l) {
		if (l == null) {
			return null;
		}
		String[] a = new String[l.size()];
		for (int i = 0; i < l.size(); i++) {
			a[i] = l.get(i);
		}
		return a;
	}

	public static boolean listIsNullOrEmpty(List<?> l) {
		return l == null || l.size() == 0;
	}

	public static String mergeList(List<?> list, String glue) {
		return mergeList(list, glue, "null");
	}

	public static String mergeList(List<?> list, String glue, String nullString) {
		if (list == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i) == null ? nullString : list.get(i).toString());
			if (i < list.size() - 1) {
				sb.append(glue);
			}
		}
		return sb.toString();
	}

	public static List<Integer> splitToIntegerList(String s, String glue, boolean addNulls) {
		List<String> ls = splitToStringList(s, glue);
		if (ls == null) {
			return null;
		}
		List<Integer> li = new ArrayList<>();
		for (int i = 0; i < ls.size(); i++) {
			try {
				String s1 = ls.get(i).trim();
				int n = Integer.parseInt(s1);
				li.add(n);
			} catch (Exception e) {
				if (addNulls) {
					li.add(null);
				}
			}
		}
		return li;
	}

	public static List<Integer> arrayToList(int[] arr) {
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < arr.length; i++) {
			l.add(arr[i]);
		}
		return l;
	}

	public static <T> List<T> arrayToList(T[] arr) {
		List<T> l = new ArrayList<T>();
		for (int i = 0; i < arr.length; i++) {
			l.add(arr[i]);
		}
		return l;
	}

	public static String[] listToArray(List<String> list) {
		if (list == null) {
			return null;
		}
		String[] o = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			o[i] = list.get(i);
		}
		return o;
	}

	public static String echapperListStringPourHSql(List<String> list) {
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			l.add(ToolBox.echapperStringPourHSql(list.get(i)));
		}
		return mergeList(l, ", ");
	}

	public static List<String> splitToStringList(String s, String glue) {
		if (s == null) {
			return null;
		}
		String[] t = s.split(glue);
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {
			a.add(t[i]);
		}
		return a;
	}

	public static String listStringStartLike(List<String> list, String col) {
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i).trim().replaceAll("'", "''");
			l.add(col + " like '" + s + "%'");
		}
		return "(" + mergeList(l, " or ") + ")";
	}

	public static <T> List<T> subList(List<T> l, int[] indices) {
		if (l == null) {
			return null;
		}
		List<T> s = new ArrayList<>();
		for (int i = 0; i < indices.length; i++) {
			int n = indices[i];
			if (n >= 0 && n < l.size()) {
				s.add(l.get(n));
			}
		}
		return s;
	}

	public static List<String> removeNotNumbersFromStringList(List<String> ids) {
		if(ids==null)return ids;
		for (int i = 0; i < ids.size(); i++) {
			String s = ids.get(i);
			try{
				Integer.parseInt(s);
			}catch(NumberFormatException e){
				ids.remove(i);
				i--;
			}
		}
		return ids;
	}

	public static String mergeIntArray(int[] ints, String glue) {
		StringBuilder sb = new StringBuilder();
		if(ints!=null)for (int i = 0; i < ints.length; i++) {
			sb.append(ints[i]);
			if (i < ints.length - 1) {
				sb.append(glue);
			}
		}
		return sb.toString();
	}

	public static boolean integerListContains(List<Integer> listeIds, Integer id) {
		if(listeIds==null)return false;
		System.out.println("integerListContains ? "+listeIds+" "+id);
		for (int i = 0; i < listeIds.size(); i++) {
			Integer get = listeIds.get(i);
			if(get==null&&id==null)return true;
			if(get!=null&&id!=null&&get.intValue()==id.intValue())return true;
		}
		return false;
	}

	public static Object[][] mergeInArray(List<Object[][]> datas) {
		int length=0;
		if(datas==null)return null;
		for (int i = 0; i < datas.size(); i++) {
			Object[][] t = datas.get(i);
			length+=t.length;
		}
		Object[][] a=new Object[length][];
		int n=0;
		for (int i = 0; i < datas.size(); i++) {
			Object[][] t = datas.get(i);
			for (int j = 0; j < t.length; j++) {
				Object[] r = t[j];
				a[n]=r;
				n++;
			}
		}
		return a;
	}
}
