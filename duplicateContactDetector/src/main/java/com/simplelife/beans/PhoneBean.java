package com.simplelife.beans;

public class PhoneBean {

	String type;
	String number;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o == this) return true; //if both pointing towards same object on heap

            PhoneBean a = (PhoneBean) o;
        return this.number.equals(a.number);
    }
	
}
