package com.simplelife.beans;

import java.util.ArrayList;
import java.util.List;

public class ContactsMainBean {
	
	String contact_id;
	String name;
	private List<PhoneBean> phonelist = new ArrayList<PhoneBean>();
	private List<EmailBean> emaillist = new ArrayList<EmailBean>();
	private List<AddressBean> addressbean = new ArrayList<AddressBean>();
	private List<String> notesList = new ArrayList<String>();
	
	public String getContact_id() {
		return contact_id;
	}
	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}
	public List<String> getNotesList() {
		return notesList;
	}
	public void setNotesList(List<String> notesList) {
		
		if(this.notesList!=null && this.notesList.size()>0)
		{
			this.notesList.addAll(notesList);
		}else{
			this.notesList = notesList;	
		}
		//this.notesList = notesList;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PhoneBean> getPhonelist() {
		return phonelist;
	}
	
	public void setPhonelist(List<PhoneBean> phonelist) {
		if(this.phonelist!=null && this.phonelist.size()>0)
		{
			this.phonelist.addAll(phonelist);
		}else{
			this.phonelist = phonelist;	
		}
		
	}
	public List<EmailBean> getEmaillist() {
		
		return emaillist;
	}
	
	public void setEmaillist(List<EmailBean> emaillist) {
		if(this.emaillist!=null && this.emaillist.size()>0)
		{
			this.emaillist.addAll(emaillist);
		}else{
			this.emaillist = emaillist;	
		}
		//this.emaillist = emaillist;
	}
	public List<AddressBean> getAddressbean() {
		return addressbean;
	}
	public void setAddressbean(List<AddressBean> addressbean) {
		
		if(this.addressbean!=null && this.addressbean.size()>0)
		{
			this.addressbean.addAll(addressbean);
		}else{
			this.addressbean = addressbean;	
		}
		//this.addressbean = addressbean;
	}
	

}
