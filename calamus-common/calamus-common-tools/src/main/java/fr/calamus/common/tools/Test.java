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
public class Test {
	public static void main(String[] args) {
		List<Integer>l=new ArrayList<>();
		l.add(16);
		l.add(52);
		String bb = Binaries.integerListToBigBinary(l);
		System.out.println("big binary : "+bb);
		System.out.println("reverse : "+Binaries.bigBinaryToIntegerList(bb));
		String r1 = Binaries.bigBinaryToHexa(bb);
		System.out.println("to hex : "+r1);
		System.out.println("reverse : "+Binaries.hexaToBigBinary(r1));
		System.out.println(" -> : "+Binaries.bigBinaryToIntegerList(Binaries.hexaToBigBinary(r1)));
		/*long lo = Binaries.convertBinaryToLong(bb);
		System.out.println("to long : "+lo);
		System.out.println("reverse : "+Binaries.bigBinaryToIntegerList(Binaries.convertLongToBinary(lo)));*/
	}
}
