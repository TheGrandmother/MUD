package yolo.ioopm.mud.generalobjects;


import yolo.ioopm.mud.generalobjects.Item;

class ItemContainer{
	Item item;
	int amount;
	
	public ItemContainer(Item item) {
		this.item = item;
		amount = 1;
	}
	
	Item getItem(){
		return item;
	}
	
	String getName(){
		return item.getName();
	}
	
	Item.Type getType(){
		return item.getType();
	}
	
	int getAmount(){
		return amount; 
	}
	
	void setAmount(int amount){
		this.amount = amount;
	}
	
}