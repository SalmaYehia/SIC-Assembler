package sicAssembler;

import java.util.Hashtable;
import java.util.LinkedList;

public class LiteralTable {

	private LinkedList<String> name;
	private LinkedList<String> value;
	private LinkedList<Integer> length;
	private LinkedList<Integer> address;
	private Hashtable<String, Integer> hash;
	private int startingIndex;

	// get the index of the literal

	public LiteralTable() {
		name = new LinkedList<String>();
		value = new LinkedList<String>();
		length = new LinkedList<Integer>();
		address = new LinkedList<Integer>();
		hash = new Hashtable<String, Integer>();
		startingIndex = 0;
	}

	public void addLiteral(String name, String value, int length) {
		hash.put(name, size());
		this.name.add(name);
		this.value.add(value);
		this.length.add(length);
		this.address.add(null);
	}

	public int size() {
		return name.size();
	}

	public String getValue(String name) {
		int i = hash.get(name);
		return value.get(i);
	}

	public int getLength(String name) {
		int i = hash.get(name);
		return length.get(i);
	}

	// can be null
	public Integer getaddress(String name) {
		int i = hash.get(name);
		return address.get(i);
	}
	
	public String getValue(int i) {
		return value.get(i);
	}

	public int getLength(int i) {
		return length.get(i);
	}
	
	public void setAddress(int i, int addr) {
		address.remove(i);
		address.add(i, addr);
	}

	public String getName(int i) {
		return name.get(i);
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public void setStartingAddress(int i) {
		startingIndex = i;
	}
	
	public boolean contains(String name) {
		return hash.containsKey(name);
	}

}