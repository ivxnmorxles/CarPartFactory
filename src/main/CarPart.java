package main;

public class CarPart {
   private int id;
   private String name;
   private double weight;
   private boolean isDefective;
   
 /**
  * 
  * @param id
  * @param name
  * @param weight
  * @param isDefective
  * This constructor is in charge of initializing the variables that are later on required for the CarParts
  */
    public CarPart(int id, String name, double weight, boolean isDefective) {
    	this.id = id;
    	this.name = name;
    	this.weight = weight;
    	this.isDefective = isDefective;
        
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
    	this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    public double getWeight() {
      return weight;
    }
    public void setWeight(double weight) {
    	this.weight = weight;
    }

    public boolean isDefective() {
      return isDefective;
    }
    public void setDefective(boolean isDefective) {
    	this.isDefective = isDefective;
    }
    /**
     * Returns the parts name as its string representation
     * @return (String) The part name
     */
    public String toString() {
        return this.getName();
    }
}