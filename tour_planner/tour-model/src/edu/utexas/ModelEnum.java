package edu.utexas;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.sat4j.core.VecInt;
import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;


public class ModelEnum {
	
	private static IPBSolver solver;
	private static List<String> cities;
	
	public static int modelEnum(int nb_cities, int result){
		
		solver = new OptToPBSATAdapter(new PseudoOptDecorator(SolverFactory.newDefault()));
		boolean ok = true;
		int models = 0;
				
		solver.newVar(cities.size());
		VecInt vars = new VecInt();
		VecInt coeffs = new VecInt();
		
		for (int i = 1; i <= cities.size(); i++){
			vars.push(i);
			coeffs.push(cities.get(i-1).length());
		}
		
		try {
			// Subset sum: 6*Boston+9*Worcester+...+6*Lowell=result
			solver.addExactly(vars, coeffs, result);
			// Exactly n cities: Boston+Worcester+...+Lowell=nb_cities
			solver.addExactly(vars, nb_cities);
		} catch (ContradictionException e) {
			ok = false;
		}
		
		if (ok) {
			try {
				while (solver.isSatisfiable()) {
					models++;
					// block model
					assert (solver.model().length > 0);
					VecInt clause = new VecInt();
					for (int i = 0; i < cities.size(); i++){
						clause.push(-solver.model()[i]);
					}
					
					try {
						solver.addClause(clause);
					} catch (ContradictionException e) {
						break;
					}
				}
			} catch (TimeoutException e) {
				assert false;
			}
		}
		return models;
	}
	
	public static void main(String[] args) throws Exception {
		
		cities = new ArrayList<String>();
		int min_size = 0;
		int total_size = 0;
		int nb_cities = Integer.parseInt(args[1]);
		
		// read file and build constraint system
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		try {
		    String line = br.readLine();
		    while (line != null) {
		    	cities.add(line);
		    	if (min_size == 0 || min_size > line.length())
		    		min_size = line.length();
		    	total_size += line.length();
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}

		for (int i = min_size; i <= total_size; i++){
			System.out.println("Cities= " + nb_cities + " Goal= " + i + " Models= " + modelEnum(nb_cities,i));
		}
		
	}

}
