package de.abd.mda.persistence.dao;

public class InvoiceConfiguration extends DaoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 46437276670900355L;
	private int id;
	private int simPrice;
	private int dataOptionSurcharge;
	private String format;
	private String[] columns;
	private String creationFrequency;
	
	public InvoiceConfiguration() {
		this.format = "Hochformat";
		this.columns = new String[5];
//		columns[0] = "1";
//		columns[1] = "3";
		this.creationFrequency = "monatlich";
	}

	public int getSimPrice() {
		return simPrice;
	}

	public void setSimPrice(int simPrice) {
		this.simPrice = simPrice;
	}

	public int getDataOptionSurcharge() {
		return dataOptionSurcharge;
	}

	public void setDataOptionSurcharge(int dataOptionSurcharge) {
		this.dataOptionSurcharge = dataOptionSurcharge;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCreationFrequency() {
		return creationFrequency;
	}

	public void setCreationFrequency(String invoiceCreationFrequency) {
		this.creationFrequency = invoiceCreationFrequency;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
