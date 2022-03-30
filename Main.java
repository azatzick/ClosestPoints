
//package ClosestSchools;


/*
* Author: Alina Zatzick
* Implements the closest pair of points recursive algorithm
* on locations of K-12 schools in Vermont obtained from http://geodata.vermont.gov/datasets/vt-school-locations-k-12
*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.io.File;


public class Main {

	//two global variables
	public static double delta = Double.MAX_VALUE;									//holds our minimum distance between two points, delta

	public static School [] closestPair = new School[2];						// holds our array of two schools that will be our closest pair


	public static void main(String[] args) throws IOException{

		//Creates an ArrayList containing School objects from the .csv file
		// Based on https://stackoverflow.com/questions/49599194/reading-csv-file-into-an-arrayliststudent-java
		String line = null;
		ArrayList <School> schoolList = new ArrayList <School>();

		// You may have to adjust the file address in the following line to your computer
		BufferedReader br = new BufferedReader(new FileReader("Data/VT_School_Locations__K12(1).csv"));
		if ((line=br.readLine())==null){
			return;
		}


		while ((line = br.readLine())!=null) {
			String[] temp = line.split(",");
			schoolList.add(new School(temp[4],Double.parseDouble(temp[0]),Double.parseDouble(temp[1])));
		}


		//Preprocess the data to create two sorted arrayLists (one by X-coordinate and one by Y-coordinate):
		ArrayList<School> Xsorted = new ArrayList <School>();
		ArrayList<School> Ysorted = new ArrayList <School>();
		Collections.sort(schoolList, new SortbyX());
		Xsorted.addAll(schoolList);
		Collections.sort(schoolList, new SortbyY());
		Ysorted.addAll(schoolList);

	//	Run the Recursive Algorithm
		School[] cp = new School[2];
		cp = ClosestPoints(Xsorted,Ysorted);
		if(cp[0]!=null)
			System.out.println("The two closest schools are "+ cp[0].name + " and " + cp[1].name +".");

	}

	public static School[] ClosestPoints(ArrayList<School> sLx, ArrayList<School> sLy){
		// Recursive divide and conquer algorithm for closest points
		// sLx should be sorted by x coordinate and sLy should be sorted by y coordinate
		// Returns an array containing the two closest School objects

	//------------------------- BASE CASE -------------------------

	//when the size of our array list is < = 3
	//calls brute force function

 	if (sLx.size() <= 3){
		closestPair = brute_force(sLx,closestPair);
		return closestPair;
	 }

	 //------------------------- RECURSIVE CASE -------------------------

	 // midline index in our array list
	 int midline = Math.floorDiv(sLx.size(), 2);

	 // indicates the school at our midline index
	 School midpoint = sLx.get(midline);


		// DIVISON STEP
		//intitializing 4 different array lists to hold our Left x, Right x, Left, y, and Right y

	 //x-sorted sublists
	 ArrayList <School> xLeft = new ArrayList <School>(sLx.subList(0,midline));

	 ArrayList <School> xRight = new ArrayList <School>(sLx.subList(midline, sLx.size()-1));

	 //y-sorted sublists
	 ArrayList <School> yLeft = new ArrayList <School>();
	 ArrayList <School> yRight = new ArrayList <School>();

	 //y-sorted divided based on the x coordinate
	 for (int i = 0; i <sLy.size(); i++){
		 if (xLeft.contains(sLy.get(i))){									// if the Left x contains this school --> Left y should too
			 yLeft.add(sLy.get(i));
		 }
		 else{
			 yRight.add(sLy.get(i));
		 }
		}



		//RECURSIVE STEP
		// calling ClosestPoints on our Left anf Right points
		School [] CloPtLeft =  ClosestPoints(xLeft, yLeft);
	 	School [] CloPtRight = ClosestPoints(xRight, yRight);

		// finding delta values in each half
		double deltaL = dist(CloPtLeft[0], CloPtLeft[1]);
		double deltaR = dist(CloPtRight[0], CloPtRight[1]);

		double delta_rec = Math.min(deltaL, deltaR);							//comparing the delta left and right
		delta = Math.min(delta, delta_rec);												// comparing with our global delta variable


		//MIDLINE STEP
	 //creating array list to hold schools closest to midline
	 ArrayList <School> mid_points = new ArrayList <School>();

	 //feeding in our ysorted array
	 for (int i = 0; i < sLy.size(); i ++){
		 School s = sLy.get(i);

		 if(Math.abs(s.xpos - midpoint.getX()) < delta){					//finding points < delta from midline
			 mid_points.add(s);
		 }
	 }

	 //Looking at the closest 7 neighbors
	 // if there are less than seven points in the arrayList, we look until we get to the end
	 	for (int i = 0; i < mid_points.size(); i ++){
			int seven = (i + 7);
			for (int j = (i +1); j < Math.min(seven, mid_points.size()); j ++){
				double point_dist = dist(mid_points.get(i), mid_points.get(j));
				if (point_dist < delta){
					delta = point_dist;
					closestPair [0] = mid_points.get(i);
					closestPair [1] = mid_points.get(j);
				}
			}
		}

	 return closestPair;

	}

// Parameters:
//     - sorted X array list containing schools
//     - closestPair [] that contains the existing two closest schools
	public static School[] brute_force (ArrayList<School> sLx, School [] closestPair){
// Brute force function that checks every element in the array list, comparing the distance
// to each other element. Returns the closestPair [].

		for (int i = 0; i < sLx.size(); i ++) {
			for (int j = i +1; j < sLx.size(); j++){
				double point_dist = dist(sLx.get(i), sLx.get(j));
				if (point_dist < delta) {
					closestPair[0] = sLx.get(i);
					closestPair[1] = sLx.get(j);
				}
			}
		}
		return closestPair;
	}


//Parameters
//       - Two schools (in the form of arrays)
	public static double dist(School first, School second){
// Implements the distance formula, returing the double distance
// between two schools

		double p1x = first.xpos;
		double p1y = first.ypos;
		double p2x = second.xpos;
		double p2y = second.ypos;

		double distance = Math.sqrt((p2y - p1y) * (p2y - p1y) + (p2x - p1x) * (p2x - p1x));
		return distance;

 }


}
