package com.simplelife.beans;

import java.util.List;

public class Contactbean {
	
	int contactid;
	
	List<String> numbers;
	
	public List<String> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<String> numbers) {
		this.numbers = numbers;
	}

	String contactName;
	
	String contactNumber;
	
	String email;
	
	String lookupkey;
	
	String createddate;

	public String getCreateddate() {
		return createddate;
	}

	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}

	public String getLookupkey() {
		return lookupkey;
	}

	public void setLookupkey(String lookupkey) {
		this.lookupkey = lookupkey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getContactid() {
		return contactid;
	}

	public void setContactid(int contactid) {
		this.contactid = contactid;
	}

	public String getContactName() {
		if(contactName==null || contactName.equals("null"))
		{
			return  "Empty Contact";
		}
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	 public String toString() {
	        return this.contactName + " [" + this.contactNumber + "]";
	    }

}
