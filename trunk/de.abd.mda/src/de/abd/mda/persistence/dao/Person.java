package de.abd.mda.persistence.dao;

public class Person extends DaoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8754205128095926999L;
	private int id;
	private String name;
	private String firstname;
	private String gender;
	private Address address;
	private String phoneNrFirst;
	private String phoneNrSecond;
	private String email;
	private String de_mail;

	public Person() {
		this.name = "";
		this.firstname = "";
		this.gender = "Herr";
		// CASCADE: Evtl. hier eine Zeile auskommentieren
		this.address = new Address();
		this.phoneNrFirst = "";
		this.phoneNrSecond = "";
		this.email = "";
		this.de_mail = "";
	}

	public String getNameString() {
		String nameS = "";
		if (gender != null)
			nameS += gender + " ";
		if (firstname != null)
			nameS += firstname + " ";
		if (name != null)
			nameS += name;
		return nameS;
	}

	public String getPhoneNr() {
		if (phoneNrFirst != null && phoneNrFirst.length() > 0 && phoneNrSecond != null && phoneNrSecond.length() > 0)
			if (phoneNrFirst.startsWith("0")) {
				return phoneNrFirst + phoneNrSecond;
			} else {
				return "0" + phoneNrFirst + phoneNrSecond;
		}
		else return "";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public Address getAddress() {
		if (address != null)
			return address;
		else
			return new Address();
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoneNrFirst() {
		return phoneNrFirst;
	}

	public void setPhoneNrFirst(String phoneNrFirst) {
		this.phoneNrFirst = phoneNrFirst;
	}

	public String getPhoneNrSecond() {
		return phoneNrSecond;
	}

	public void setPhoneNrSecond(String phoneNrSecond) {
		this.phoneNrSecond = phoneNrSecond;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDe_mail() {
		return de_mail;
	}

	public void setDe_mail(String de_mail) {
		this.de_mail = de_mail;
	}

}