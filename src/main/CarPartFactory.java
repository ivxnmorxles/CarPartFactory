package main;

import interfaces.List;
import interfaces.Map;
import interfaces.Stack;
import data_structures.BasicHashFunction;
import data_structures.DoublyLinkedList;
import data_structures.HashTableSC;
import data_structures.LinkedStack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class CarPartFactory {
	private List<PartMachine>machines;
	private Stack<CarPart> production;
	private Map<Integer, CarPart> partCatalog;
	private Map<Integer, List<CarPart>> inventory;
	private List<Order> orders;
	private Map<Integer, Integer> defectives;
	private BufferedReader ordersReader = new BufferedReader(new FileReader("input/orders.csv"));
	
	/***
	 * 
	 * @param orderPath
	 * @param partsPath
	 * @throws IOException
	 * This constructor is in charged of receiving the paths of the .csv files, it is also in charged of passing the
	 * setupInventory method and initializing the production stack
	 */
        
    public CarPartFactory(String orderPath, String partsPath) throws IOException {
    	this.setupMachines(partsPath);
    	this.setupOrders(orderPath);
    	this.setupInventory();
    	this.production = new LinkedStack<>();
    }
    public List<PartMachine> getMachines() {
       return machines;
    }
    public void setMachines(List<PartMachine> machines) {
        this.machines = machines;
    }
    public Stack<CarPart> getProductionBin() {
      return production;
    }
    public void setProductionBin(Stack<CarPart> production) {
       this.production = production;
    }
    public Map<Integer, CarPart> getPartCatalog() {
        return partCatalog;
    }
    public void setPartCatalog(Map<Integer, CarPart> partCatalog) {
        this.partCatalog = partCatalog;
    }
    public Map<Integer, List<CarPart>> getInventory() {
       return inventory;
    }
    public void setInventory(Map<Integer, List<CarPart>> inventory) {
        this.inventory = inventory;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public Map<Integer, Integer> getDefectives() {
        return defectives;
    }
    public void setDefectives(Map<Integer, Integer> defectives) {
        this.defectives = defectives;
    }
    /***
     * 
     * @param path
     * @throws IOException
     * This method is in charge of recieving the data from the orders.csv file and parsing it into a DoublyLinkedList
     * in order to be called upon for other methods,I started off by skipping the header line and verifying that the 
     * line wasnt null, if it wasnt null we start parsing data, the HashTable is done for when we later on call upon the requestedParts
     * info.
     */
    public void setupOrders(String path) throws IOException {
    	orders = new DoublyLinkedList<Order>();
    	String line = this.ordersReader.readLine();
    	line = this.ordersReader.readLine();
    	while(line!= null) {
    		 String[]orderSplit=line.split(",");
    		 int orderId = Integer.parseInt(orderSplit[0]);
             String customerName = orderSplit[1];
             String[] requestedParts = orderSplit[2].split("-");
             Map<Integer, Integer> requestedPartsMap = new HashTableSC<Integer,Integer>(requestedParts.length,new BasicHashFunction());
            int i =0;
             while(i < requestedParts.length) {
            	 String partInfo = requestedParts[i];
                 String[] partDetails = partInfo.trim().replaceAll("\\(|\\)", "").split(" ");
                 int partId = Integer.parseInt(partDetails[0]);
                 int quantity = Integer.parseInt(partDetails[1]);
                 requestedPartsMap.put(partId, quantity);
                 i++;
             }
             line = this.ordersReader.readLine();
             orders.add(new Order(orderId, customerName, requestedPartsMap, false));
             }
    }
    /**
     * 
     * @param path
     * @throws IOException
     * 
     * This method is in char of reading the data from the parts.csv file and parsing it to a list, its also
     * responsible for the creation of the partCatalog which is required for other methods, the parsing of data
     * is done by skipping the header file, count how many lines there is so that when the Hash Fucntion is created 
     * its initial capacity can  have its value, at the end we add those parts created to the machine list and part catalog
     */
    public void setupMachines(String path) throws IOException {
    	machines = new DoublyLinkedList<PartMachine>();
    	BufferedReader partsReader = new BufferedReader(new FileReader(path));
       String line = partsReader.readLine();
       long lineCounter = partsReader.lines().count();
       partCatalog = new HashTableSC<Integer,CarPart>((int) lineCounter, new BasicHashFunction());
       partsReader.close();
       partsReader = new BufferedReader(new FileReader(path));
       line = partsReader.readLine();
       line = partsReader.readLine();
       
       while(line!=null) {
    	   String[]parts=line.split(",");
    	   int carPartId = Integer.parseInt(parts[0]);
    	   String partName = parts[1];
    	   double partWeight = Double.parseDouble(parts[2]);
    	   double weightError = Double.parseDouble(parts[3]);
           int period = Integer.parseInt(parts[4]);
           int chanceOfDefective = Integer.parseInt(parts[5]);
           boolean isDefective = false;
           
           CarPart newPart = new CarPart(carPartId, partName, partWeight, isDefective);
           partCatalog.put(carPartId, newPart);
           
           PartMachine newMachines = new PartMachine(carPartId,newPart,period,weightError,chanceOfDefective);
           machines.add(newMachines);
           line = partsReader.readLine();
    		}
       partsReader.close();
       
    }
    //public void setupCatalog() {
        
    //}
    /**
     * created and initialized the inventory variable , also inserted to the inventory the Id of the part and the list
     * of future values
     */
    public void setupInventory() {
    	if(inventory ==null) {
    	inventory = new HashTableSC<>(partCatalog.size(), new BasicHashFunction());
    	}
    	//List<CarPart> invList = new DoublyLinkedList<>();
    	for(int invPartId : partCatalog.getKeys()){
    		inventory.put(invPartId,new DoublyLinkedList());
    	}
    }
    /**
     * Method is in charge of stashing the parts in a production bin , where we also check if the parts are 
     * defective, and if they are defective we increase a counter and do not include those parts in the inventory
     * this code at the end adds those inventory parts to a list, which is added to the inventory HashFunc created before
     */
    public void storeInInventory() {
    	if(defectives == null) {
    		defectives = new HashTableSC<>(partCatalog.size(), new BasicHashFunction());
    	}
      while(!production.isEmpty()) {
    	  CarPart prodBinPart = production.pop();
    	  int idInBin = prodBinPart.getId();
    	  if(prodBinPart.isDefective()) {
    		  if(defectives.containsKey(idInBin)) {
    			  int counterOfDefect = defectives.get(idInBin)+1;
    			  defectives.put(idInBin, counterOfDefect);
    		  }else {
    			  defectives.put(idInBin, 1);
    		  }
    	  }else {
    		  List<CarPart> invList = inventory.get(idInBin);
    		  if(invList==null) {
    			  invList = new DoublyLinkedList<>();
    			  inventory.put(idInBin, invList);

    		  }
    		  invList.add(prodBinPart);
    	  }
        
    }
   }
    /**
     * 
     * @param days
     * @param minutes
     * This method goes through the days and minutes and at the end it gives the total of the ordersProcessed 
     * and also calls the storeInInventory after each day, it also makes use of a conveyor belt to dequeue the productionBin
     * at the end of the day it resets the belt
     */
    public void runFactory(int days, int minutes) {
        int i = 0;
        while (i < days) {
            int j = 0;
            while (j < minutes) {
                for (PartMachine machine : machines) {
                    CarPart producedPart = machine.produceCarPart();
                    if (producedPart != null) {
                        production.push(producedPart);
                    }
                }
                j++;
            }
            i++;
        }

        for (PartMachine machine : machines) {
            while (!machine.getConveyorBelt().isEmpty()) {
                CarPart frontPart = machine.getConveyorBelt().front();
                if (frontPart != null) {
                    production.push(machine.getConveyorBelt().dequeue());
                } else {
                    machine.getConveyorBelt().dequeue();
                }
            }
            machine.resetConveyorBelt();
        }
        storeInInventory();
        processOrders();
    }

/**
 * This method goes through orders list, later it goes through the HashFunction done in the orders list to get the id 
 * parts needed, it also checks if that id is present in the inventory and also checks how many parts are needed
 * if the parts needed exceed the available parts in the Inventory it makes the boolean variable false
 */
    public void processOrders() {
        for (Order orderIterator : this.getOrders()) {
            boolean truelyFulfilled = true;
            for (int partId : orderIterator.getRequestedParts().getKeys()) {
                if (this.getInventory().containsKey(partId)) {
                    int availableParts = this.getInventory().get(partId).size();
                    int neededParts = orderIterator.getRequestedParts().get(partId);
                    if (neededParts > availableParts) {
                        truelyFulfilled = false;
                        break;
                    }
                } else {
                    truelyFulfilled = false;
                    break;
                }
            }
            if (truelyFulfilled) {
                orderIterator.setFulfilled(true);
                for (int partId : orderIterator.getRequestedParts().getKeys()) {
                    List<CarPart> partList = this.getInventory().get(partId);
                    int requiredPartsInv = orderIterator.getRequestedParts().get(partId);
                    for (int i = partList.size() - 1; i >= 0 && requiredPartsInv > 0; i--) {
                        partList.remove(i);
                        requiredPartsInv--;
                    }
                }
            }else {
                orderIterator.setFulfilled(false);
            }
        }
    }




    /**
     * Generates a report indicating how many parts were produced per machine,
     * how many of those were defective and are still in inventory. Additionally, 
     * it also shows how many orders were successfully fulfilled. 
     */
    public void generateReport() {
        String report = "\t\t\tREPORT\n\n";
        report += "Parts Produced per Machine\n";
        for (PartMachine machine : this.getMachines()) {
            report += machine + "\t(" + 
            this.getDefectives().get(machine.getPart().getId()) +" defective)\t(" + 
            this.getInventory().get(machine.getPart().getId()).size() + " in inventory)\n";
        }
       
        report += "\nORDERS\n\n";
        for (Order transaction : this.getOrders()) {
            report += transaction + "\n";
        }
        System.out.println(report);
    }

   

}
