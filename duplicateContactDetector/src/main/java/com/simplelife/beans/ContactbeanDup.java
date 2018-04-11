package com.simplelife.beans;

public class ContactbeanDup {
	
	int contactid;
	
	int count;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	String contactName;
	
	String contactNumber;
	
	String email;
	
	String lookupkey;
	
	String createddate;
	
	String type;
	
	String typeid;

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
	        return this.contactName +"[ " + this.contactNumber + " ]";
	    }

}
