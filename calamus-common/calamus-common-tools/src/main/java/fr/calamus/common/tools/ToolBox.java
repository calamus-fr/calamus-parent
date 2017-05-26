package fr.calamus.common.tools;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ToolBox {

	private static SimpleDateFormat formatteurDateSimple;

	/**
	 * Méthode ouvrant un lien hypertexte dans le navigateur par défaut.
	 *
	 * @param url
	 */
	public static void browse(String url) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(url));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			} else {
				log("browse not supported");
			}
		} else {
			log("desktop not supported");
		}
	}


	public static void log(String s, Object o) {
		log((o == null || o.getClass() == null) ? "null" : (o instanceof Class ? ((Class<?>) o).getSimpleName() : o
			.getClass().getSimpleName()) + " : " + s);
	}

	public static void log(String s) {
		System.out.println(s);
	}
	/*public static boolean isFullOfNumbers(String s) {
		if (s == null) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			boolean isNumber = false;
			try {
				int n = Integer.parseInt("" + s.charAt(i));
				isNumber = true;
			} catch (Exception e) {

			}
			if (!isNumber) {
				return false;
			}
		}
		return true;
	}*/
	// Déterminer la version de Java
	public static double versionJava() {
		String version = System.getProperty("java.specification.version");
		double v = Double.valueOf(version).doubleValue();
		System.out.println(version);
		return v;
	}

	public static String echapperStringPourMySql(String s) {
		if (s == null) {
			return null;
		}
		s = s.replace("\"", "\\\"");
		return "\"" + s + "\"";
	}

	public static void printBytes(byte[] array, String name) {
		for (int k = 0; k < array.length; k++) {
			System.out.println(name + "[" + k + "] = " + "0x"
				+ UnicodeFormatter.byteToHex(array[k]));
		}
	}

	public static void printBytes(String name) {
		byte[] array = name.getBytes();
		for (int k = 0; k < array.length; k++) {
			System.out.println(name + "[" + k + "] = " + "0x"
				+ UnicodeFormatter.byteToHex(array[k]));
		}
	}

	public static String echapperStringPourHSql(String s) {
		if (s == null) {
			return "null";
		}
		s = s.replace("'", "''");
		return "'" + s + "'";
	}


	@SuppressWarnings("finally")
	public static List<String> post(String adress, List<String> keys, List<String> values) throws IOException {
		// String result = "";
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		List<String> l = new ArrayList<String>();
		try {
			// encodage des paramètres de la requête
			String data = "";
			for (int i = 0; i < keys.size(); i++) {
				if (i != 0) {
					data += "&amp;";
				}
				data += URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
			}
			// création de la connection
			URL url = new URL(adress);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			// envoi de la requête
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();

			// lecture de la réponse
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				// result+=ligne;
				l.add(ligne);
			}
		} catch (Exception e) {
			e.printStackTrace();
			l = null;
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
			try {
				reader.close();
			} catch (Exception e) {
			}
			return l;
		}

	}

	public static List<String> get(String url) throws IOException {
		List<String> l = new ArrayList<>();
		// String source ="";
		URL oracle = new URL(url);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			// source +=inputLine;
			l.add(inputLine);
		}
		in.close();
		return l;
	}


	@SuppressWarnings("deprecation")
	public static String echapperDateSeulePourHsql(Date d) {
		if (d == null) {
			return "null";
		}
		return "'" + (d.getYear() + 1900) + "-" + (d.getMonth() + 1) + "-" + d.getDate() + "'";
	}

	@SuppressWarnings("deprecation")
	public static String echapperDatePourHsql(Date d) {
		if (d == null) {
			return "null";
		}
		if (d instanceof java.sql.Date) {
			d = new java.util.Date(d.getYear(), d.getMonth(), d.getDate(), 0, 0, 0);
		}
		String s = "'";
		s += (d.getYear() + 1900) + "-" + (d.getMonth() + 1) + "-" + d.getDate() + " ";
		s += d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + "'";
		return s;
	}


	/*public static Vector<String> trierColonneEffectifs(Vector<String> v) {
		//log("trierColonneEffectifs");
		Comparator<String> comp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// log("comp "+o1+" "+o2);
				if (o1 == null && o2 == null) {
					return 0;
				} else if (o1 == null && o2 != null) {
					return -1;
				} else if (o1 != null && o2 == null) {
					return 1;
				} else if (o1.equals("") && o2.equals("")) {
					return 0;
				} else if (o1.equals("") && !o2.equals("")) {
					return -1;
				} else if (!o1.equals("") && o2.equals("")) {
					return 1;
				} else {
					// log(" valeurs non nulles et non vides");
					String[] t1 = o1.split(" ");
					String[] t2 = o2.split(" ");
					int n1 = 0;
					int n2 = 0;
					try {
						n1 = Integer.parseInt(t1[0]);
					} catch (NumberFormatException e) {
						return -1;
					}
					try {
						n2 = Integer.parseInt(t2[0]);
					} catch (NumberFormatException e) {
						return 1;
					}
					// log("  "+n1+" "+n2);
					if (n1 > n2) {
						return 1;
					} else if (n1 < n2) {
						return -1;
					}
				}
				return 0;
			}
		};
		Collections.sort(v, comp);
		// if(v.size()>)v.remove(0);
		return v;
	}*/

	public static boolean checkInternetConnection() {
		boolean b;
		try {
			URLConnection cnx = new URL("http://www.google.fr").openConnection();
			cnx.connect();
			b = true;
		} catch (IOException e) {
			b = false;
		}
		//log("checkInternetConnection:" + b);
		return b;
	}


	public static boolean mapIsNullOrEmpty(Map<?, ?> m) {
		return m == null || m.size() == 0;
	}

	public static long convertBinaryToLong(String binaryValue) {
		long valueI;
		try {
			valueI = Long.parseLong(binaryValue, 2);
		} catch (NumberFormatException e) {
			// errorMsg = "Erreur de conversion (binaire - décimale)";
			return Long.MIN_VALUE;
		}
		return valueI;
	}

	public static void main(String[] args) {
		System.out.println(doublerAntislashes(echapperQuotes("O'Reole")));
	}

	public static SimpleDateFormat getFormatteurDateSimpleFr() {
		if (formatteurDateSimple == null) {
			formatteurDateSimple = new SimpleDateFormat("dd/MM/YYYY");
		}
		return formatteurDateSimple;
	}

	public static boolean isEmpty(String s) {
		if(s==null)return true;
		return s.trim().isEmpty();
	}

	public static boolean isEmpty(int[] s) {
		return s == null || s.length == 0;
	}

	public static <T> boolean isEmpty(T[] s) {
		return s == null || s.length == 0;
	}

	public static String getStringValue(Object o) {
		if (o instanceof Integer) {
			return "" + o;
		}
		if (o instanceof String) {
			return ToolBox.echapperStringPourHSql((String) o);
		}
		if (o instanceof Date) {
			return ToolBox.echapperDatePourHsql((Date) o);
		}
		return null;
	}

	public static boolean checkNotNull(Class c, Object o, String name) {
		if (o == null) {
			log(name + " is null!", c);
			return false;
		}
		return true;
	}

	public static boolean isFullOfNumbers(String s) {
		return isFullOfNumbers(s, false);
	}
	public static boolean isFullOfNumbers(String s, boolean acceptSpace) {
		if (s == null) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			boolean isNumber = false;
			try {
				int n = Integer.parseInt("" + s.charAt(i));
				isNumber = true;
			} catch (Exception e) {
				if(acceptSpace&&s.charAt(i)==' ')isNumber=true;
			}
			if (!isNumber) {
				return false;
			}
		}
		return true;

	}
	/**
	 * to lowercase, enleve accents, espaces en trop et ponctuation + trim
	 * @param s
	 * @return
	 */
	public static String simplifierString(String s){
		if(s==null)return "";
		s=s.toLowerCase();
		s=enleverAccents(s);
		s=enleverPonctuation(s);
		s=enleverEspacesEnTrop(s);
		s=s.trim();
		return s;
	}
	public static String enleverAccents(String s){
		return enleverAccents(s, true);
	}
	public static String enleverAccents(String s, boolean toLowerCase){
		if(s==null)return null;
		if(toLowerCase)s=s.toLowerCase();
		List<String> l1 = ListsAndArrays.splitToStringList("éèêë,àâáãåä,æ,œ,ìíîï,òóôöõø,úùûü,ÿŷ,ñ,ç", ",");
		List<String> l2 = ListsAndArrays.splitToStringList("e,a,ae,oe,i,o,u,y,n,c", ",");
		for(int i=0;i<l1.size();i++){
			s=s.replaceAll("["+l1.get(i)+"]", l2.get(i));
			if(!toLowerCase)s=s.replaceAll("["+l1.get(i).toUpperCase()+"]", l2.get(i).toUpperCase());
		}
		/*s=s.replaceAll("[éèêë]", "e");
		s=s.replaceAll("[àâáãåä]", "a");
		s=s.replaceAll("æ", "ae");
		s=s.replaceAll("[ìíîï]", "i");
		s=s.replaceAll("[òóôöõø]", "o");
		s=s.replaceAll("[úùûü]", "u");
		s=s.replaceAll("ñ", "n");
		s=s.replaceAll("ç", "c");*/
		return s;
	}

	public static String enleverPonctuation(String s) {
		if(s==null)return null;
		s=s.replaceAll("-", " ");
		s=s.replaceAll("[\"(),;:!?./\b\t\n\f\r\'\\{}]", " ");// TODO ajouter espaces insécables
		s=s.replaceAll(" +", " ");
		//s=s.replaceAll("  ", " ");
		s=s.trim();
		return s;
	}

	public static String echapperQuotesEtDoublesQuotes(String s) {
		if (s == null) {
			return "";
		}
		s = s.replaceAll("\'", "\\\'");
		s = s.replaceAll("'", "\\'");
		s = s.replaceAll("\"", "\\\"");
		return s;
	}

	public static String echapperQuotes(String s) {
		//System.out.println("\\"+"'");
		if (s == null) {
			return "";
		}
		//System.out.println("index1="+s.indexOf("\'"));
		//System.out.println("index2="+s.indexOf("'"));
		//s = s.replaceAll("\'", "\\\'");
		s = s.replace("\'", "\\\'");
		//s = s.replaceAll("\"", "\\\"");
		return s;
	}
	public static String echapperDoublesQuotes(String s) {
		//System.out.println("\\"+"'");
		if (s == null) {
			return "";
		}
		//System.out.println("index1="+s.indexOf("\'"));
		//System.out.println("index2="+s.indexOf("'"));
		//s = s.replaceAll("\'", "\\\'");
		s = s.replace("\"", "\\\"");
		//s = s.replaceAll("\"", "\\\"");
		return s;
	}

	public static String doublerAntislashes(String s) {
		if (s == null) {
			return "";
		}
		s = s.replace("\\", "\\\\");
		return s;
	}

	public static boolean stringIsNullOrEmpty(String s) {
		return s==null||s.isEmpty();
	}

	public static boolean stringIsNullOrBlank(String s) {
		return s==null||s.trim().isEmpty();
	}

	public static String enleverEspacesEnTrop(String s) {
		if(s==null)return "";
		s=s.replaceAll(" +", " ");
		return s;
	}

	public static String reverseString(String s) {
		if(s==null)return null;
		StringBuilder b=new StringBuilder();
		int l = s.length();
		for(int i=0;i<l;i++){
			b.append(s.charAt(l-i-1));
		}
		return b.toString();
	}

	public static String garderUniquementChiffres(String s) {
		if(s==null)return null;
		String nums="0123456789";
		StringBuilder b=new StringBuilder();
		for(int i=0;i<s.length();i++){
			String c=""+s.charAt(i);
			if(nums.contains(c)){
				b.append(c);
			}
		}
		return b.toString();
	}
}
