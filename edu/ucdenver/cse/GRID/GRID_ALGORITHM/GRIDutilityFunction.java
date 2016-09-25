package edu.ucdenver.cse.GRID.GRID_ALGORITHM;

/**
 * Created by MFS on 9/25/2016.
 */
public class GRIDutilityFunction {

    private Double emissions;
    private Double idealSpeed; // meters per second

    public GRIDutilityFunction(){
        emissions = 0.0;
        idealSpeed = 15.8333; // (~35 mph) to 24.4444 (~55 mph)
    }
    /*
	 * this is an approximation to compare the emissions for a given agent
	 */
    public double calcEmissions (Double currentSpeed, Double roadLength) {

        emissions = roadLength/idealSpeed + (roadLength*(currentSpeed-idealSpeed));

        /* BEGIN test output */
        /*System.out.println("ideal time: "+roadLength/idealSpeed);
        System.out.println("roadLength: "+roadLength);
        System.out.println("current speed: "+currentSpeed);
        System.out.println("negative: "+(currentSpeed-idealSpeed));
        /* System.out.println("emissions: "+emissions);*/
        /* END test output */

        return emissions;
    }
}
