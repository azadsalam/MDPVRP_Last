package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ToLatexTable {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner in  = new Scanner(new File("input_latex.csv"));
		
		while(in.hasNext())
		{
			String line = in.nextLine();
			StringTokenizer st = new StringTokenizer(line," ,");
			//ArrayList<String> words = new ArrayList<>();
			System.out.print((String)st.nextElement());
			while(st.hasMoreElements()) System.out.print(" & "+(String)st.nextElement());
			System.out.println(" \\\\ \\hline");
			//System.out.println(words.toString());
			
		}
	}

}
