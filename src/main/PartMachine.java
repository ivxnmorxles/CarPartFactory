package main;


import java.util.Random;

import data_structures.ListQueue;
import interfaces.Queue;



public class PartMachine {
   private int id;
   private CarPart p1;
   private int period;
   private double weightError;
   private int chanceOfDefective;
   private Queue<Integer> timer;
   private Queue<CarPart> conveyorBelt;
   private int count;
   /**
    * 
    * @param id
    * @param p1
    * @param period
    * @param weightError
    * @param chanceOfDefective
    * This constructor is in charge of initializing the variables that are required for PartMachine objects that will later on be used
    * it also initializes the timer and enqueues it to have an updated correct timer, it also initializes the initial conveyor belt with
    * 10 null spaces
    */
    public PartMachine(int id, CarPart p1, int period, double weightError, int chanceOfDefective) {
        this.id = id;
        this.p1 = p1;
        this.period = period;
        this.chanceOfDefective = chanceOfDefective;
        this.weightError = weightError;
        this.timer =new ListQueue<>();
        for (int i = period - 1; i >= 0; i--) {
            timer.enqueue(i);
        }
        this.conveyorBelt = new ListQueue<>();
        
        for(int i = 0; i<10; i++) {
    		conveyorBelt.enqueue(null);
    	}
    }
    public int getId() {
       return this.id;
    }
    public void setId(int id) {
    	this.id = id;
    }
    public Queue<Integer> getTimer() {
    	return timer;
    }
    public void setTimer(Queue<Integer> timer) {
        this.timer = timer;
    }
    /*private Queue<Integer>setupTimer(int period) {
    	Queue<Integer>timer = new ListQueue<>();
    	for(int i = period-1; i>=0; i--) {
    		timer.enqueue(i);
    	}
    	return timer;
    }*/
    public CarPart getPart() {
    	return this.p1;
    }
    public void setPart(CarPart part1) {
        p1 = part1;
    }
    public Queue<CarPart> getConveyorBelt() {
        return conveyorBelt;
    }
    public void setConveyorBelt(Queue<CarPart> conveyorBelt) {
    	this.conveyorBelt = conveyorBelt;
    }
    public int getTotalPartsProduced() {
         return count;
    }
    public void setTotalPartsProduced(int count) {
    	this.count = count;
    }
    public double getPartWeightError() {
        return weightError;
    }
    public void setPartWeightError(double partWeightError) {
        this.weightError = partWeightError;
    }
    /**
     * Is in charge of getting and setting the random weight, it was taken into consideration the lower decimal of 
     * the potential random weight and also the highest decimal  and then made an equation which uses thos two values
     * and the randomWeight variable created is multiplied
     * @return RandomWeight
     */
    private double getRandomWeight() {
    	Random randomWeight = new Random();
    	double lowerW = this.getPart().getWeight() - this.getPartWeightError();
    	double higherW = this.getPart().getWeight() + this.getPartWeightError();
    	
    	return lowerW+(higherW-lowerW)*randomWeight.nextDouble();
    }
    public int getChanceOfDefective() {
        return chanceOfDefective;
    }
    public void setChanceOfDefective(int chanceOfDefective) {
        this.chanceOfDefective = chanceOfDefective;
    }
    public void resetConveyorBelt() {
    	conveyorBelt.clear();
    	for(int i = 0; i < 10;i++) {
    		conveyorBelt.enqueue(null);
    	}
    	
    }
    /**
     * In charge of the timer that passes and enqueues the correct time at the end
     * @return totalTimePassed
     */
    public int tickTimer() {
       int totalTimePassed = timer.front();
       timer.enqueue(timer.dequeue());
       return totalTimePassed;
       }
    /**
     * In charge of how long it should take for a car part to be produced, it also takes into consideration when
     * creating the car parts variables such as random weight and checking if its defective, when created those parts
     * are enqueued to the conveyor belt
     * @return
     */
    public CarPart produceCarPart() {
    	int timePassed = tickTimer();
    	if(timePassed != 0) {
    		conveyorBelt.enqueue(null);
    	}else {
    		double randomW = getRandomWeight();
    		boolean isDefective;
    		int defectiveValue = count%chanceOfDefective;
    		if (defectiveValue == 0) {
    		    isDefective = true;
    		} else {
    		    isDefective = false;
    		}
    		CarPart newParts = new CarPart(p1.getId(),p1.getName(), randomW, isDefective);
    		conveyorBelt.enqueue(newParts);
    		count++;
    		
    	}
    	return conveyorBelt.dequeue();    
       
       }

    /**
     * Returns string representation of a Part Machine in the following format:
     * Machine {id} Produced: {part name} {total parts produced}
     */
    @Override
    public String toString() {
        return "Machine " + this.getId() + " Produced: " + this.getPart().getName() + " " + this.getTotalPartsProduced();
    }
    /**
     * Prints the content of the conveyor belt. 
     * The machine is shown as |Machine {id}|.
     * If the is a part it is presented as |P| and an empty space as _.
     */
    public void printConveyorBelt() {
        // String we will print
        String str = "";
        // Iterate through the conveyor belt
        for(int i = 0; i < this.getConveyorBelt().size(); i++){
            // When the current position is empty
            if (this.getConveyorBelt().front() == null) {
                str = "_" + str;
            }
            // When there is a CarPart
            else {
                str = "|P|" + str;
            }
            // Rotate the values
            this.getConveyorBelt().enqueue(this.getConveyorBelt().dequeue());
        }
        System.out.println("|Machine " + this.getId() + "|" + str);
    }
}
