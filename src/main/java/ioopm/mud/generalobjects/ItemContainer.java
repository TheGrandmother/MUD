package ioopm.mud.generalobjects;


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
	
	public void setAmount(int amount){
		this.amount = amount;
	}
	
	public void addAmount(int add){
		if(add > 0){this.amount += add;}
	}
	
}