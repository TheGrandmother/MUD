package yolo.ioopm.mud.generalobjects;


import yolo.ioopm.mud.generalobjects.Item;

public class ItemContainer{
	Item item;
	int amount;
	
	public ItemContainer(Item item) {
		this.item = item;
		amount = 1;
	}
	
	public Item getItem(){
		return item;
	}
	
	public String getName(){
		return item.getName();
	}
	
	Item.Type getType(){
		return item.getType();
	}
	
	public  int getAmount(){
		return amount; 
	}
	
	void setAmount(int amount){
		this.amount = amount;
	}
	
}