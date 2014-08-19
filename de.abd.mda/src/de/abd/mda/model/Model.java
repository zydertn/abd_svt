package de.abd.mda.model;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.abd.mda.controller.SearchCardController;
import de.abd.mda.persistence.dao.Country;
import de.abd.mda.persistence.dao.controller.ConfigurationController;
import de.abd.mda.persistence.dao.controller.CountryController;
import de.abd.mda.util.FacesUtil;

public class Model {

	private final static Logger LOGGER = Logger.getLogger(Model.class .getName()); 

	// Domain model related variables
	private List<Country> countries;
	private List<String> statusList;
	private List<String> supplierList;
		
	public static String COUNTRY_GERMANY = "DE";
	public static String COUNTRY_AUSTRIA = "AT";
	
	public static String STATUS_ACTIVE = "Aktiv";
	public static String STATUS_INACTIVE = "Inaktiv";
	public static String STATUS_DUMMY = "Dummy";
	
	public static String SUPPLIER_TELEKOM = "Deutschland";
	public static String SUPPLIER_TELEKOM_AUSTRIA = "Austria";
	
	public static String FORMAT_HOCHFORMAT = "Hochformat";
	public static String FORMAT_QUERFORMAT = "Querformat";
	
	public static String FREQUENCY_MONTHLY = "monatlich";
	public static String FREQUENCY_QUARTERLY = "quartalsweise";
	public static String FREQUENCY_HALFYEARLY = "halbj�hrlich";
	public static String FREQUENCY_YEARLY = "j�hrlich";
	
	public static String COLUMN_POS = "Pos.";
	public static String COLUMN_AMOUNT = "Monate";
	public static String COLUMN_DESCRIPTION = "Bezeichnung";
	public static String COLUMN_PLANT_NUMBER = "Anlagen Nr.";
	public static String COLUMN_INST_PLZ = "PLZ";
	public static String COLUMN_EINSATZORT = "Einsatzort";
	public static String COLUMN_INST_STREET = "Stra�e";
	public static String COLUMN_EQUIP_NR = "Equipment Nr.";
	public static String COLUMN_AUFTRAGS_NR = "Auftrags-Nr.";
	public static String COLUMN_CARD_NR = "Kartennummer";
	public static String COLUMN_TEL_NR = "Rufnummer";
	public static String COLUMN_BESTELL_NR = "Bestell-Nr.";
	public static String COLUMN_VERTRAG_NR = "Vertragsnummer";
	public static String COLUMN_BA_NR = "BA-Nummer";
	public static String COLUMN_WE_NR = "WE-Nummer";
	public static String COLUMN_COST_CENTER = "Kostenstelle";
//	public static String COLUMN_SINGLE_PRICE = "Preis";
	public static String COLUMN_TOTAL_PRICE = "Preis";

	public static String BILLING_WE_NR = "WE-Nummer";
	public static String BILLING_BESTELL_NR = "Bestell-Nr.";
	
	public static String GENDER_MAN = "Herr";
	public static String GENDER_WOMAN = "Frau";
	public static String GENDER_COMPANY = "Firma";

	public List<String> relationList;
	public static String RELATION_0 = "";
	public static String RELATION_1 = "Relation1";
	public static String RELATION_2 = "Relation2";

	public static String JAN = "Januar";
	public static String FEB = "Februar";
	public static String MAR = "M�rz";
	public static String APR = "April";
	public static String MAY = "Mai";
	public static String JUN = "Juni";
	public static String JUL = "Juli";
	public static String AUG = "August";
	public static String SEP = "September";
	public static String OCT = "Oktober";
	public static String NOV = "November";
	public static String DEC = "Dezember";
	
	
	public String cardDeAv = "Alt-Voice";
	public String cardDeAvIp = "Alt-Voice-IP";
	public String cardDeM2m = "M2M-Voice";
	public String cardDeHbn = "HBN-Data";
	public String cardDeTc = "Testkarte";
	public String cardDeSc = "Sonderkarte";
	public String cardDeRc = "Roamingkarte";
	
	public String cardDeEmptyCmt = "";
	public String cardDeAvCmt = "Unsere Karten f�r normalen Einsatz in D, ohne fixe IP";
	public String cardDeAvIpCmt = "Unsere Karten f�r normalen Einsatz in D, mit fixer IP f�r Monitoring";
	public String cardDeM2mCmt = "??? Frau Kiesenbauer, hier schauen wir noch ein mal";
	public String cardDeHbnCmt = "Unsere neue deutsche Karte f�r das HBN Modul mit fixer IP und ohne Sprachanteil";
	public String cardDeTcCmt = "Bei Einsatz als Testkarte, zB wird nicht abgerechnet";
	public String cardDeScCmt = "f�r speziellen Einsatz, z.B. als Flatratekarte";
	public String cardDeRcCmt = "deutsche Karte im Ausland im Einsatz , z.B. Hollaus Karte";

	public String cardAut1 = "�1";
	public String cardAut2 = "�3";
	public String cardAut3 = "�5";
	public String cardAut4 = "EU";
	public String cardAut5 = "NTS";
	public String cardAut6 = "HBN-EU";
	public String cardAut7 = "HBN-NTS";
	public String cardAut8 = "Testkarte";
	public String cardAut9 = "Sonderkarte";

	public String cardAutAA1 = "Austria Intern";
	public String cardAutAA2 = "Europa";

	public String paymentInvoice = "Rechnung";
	public String paymentDebit = "Lastschrift";
	
	public Map<Integer, Double> simPrices;
	public Map<Integer, Double> dataOptionSurcharges;
	
	private HashMap<String, Float> columnSize;
	private List<String> invoiceFormats;
	private List<String> invoiceCreationFrequencies;
	private List<String> invoiceColumns;
	private List<String> paymentMethods;
	private List<Integer> invoiceRowList;
	private List<String> billingCriteria;
	private HashMap<Integer, String> months;
	private List<Integer> years;
	
	private String pdfPath = "";
	private String zipPath = "";
	
	public Model() {
		LOGGER.info("Instantiate: Model");
	}

	public void createModel() {
		if (countries == null) {
			CountryController cc = new CountryController();
			countries = cc.listCountries();
		}
//		countries = new ArrayList<Country>();
//		countries.add(new Country("Deutschland", "DE", "+49"));
//		countries.add(new Country("D�nemark", "DK", "+45"));
//		countries.add(new Country("Irland", "IE", "+353"));
//		countries.add(new Country("Gro�britannien", "GB", "+44"));
//		countries.add(new Country("�sterreich", "AT", "+43"));
//		countries.add(new Country("Polen", "PL", "+48"));
//		countries.add(new Country("Rum�nien", "RO", "+40"));
//		countries.add(new Country("Tschechien", "CZ", "+420"));
		
		
		statusList = new ArrayList<String>();
		statusList.add(Model.STATUS_ACTIVE);
		statusList.add(Model.STATUS_INACTIVE);
		statusList.add(Model.STATUS_DUMMY);
		
		supplierList = new ArrayList<String>();
		supplierList.add(Model.SUPPLIER_TELEKOM);
		supplierList.add(Model.SUPPLIER_TELEKOM_AUSTRIA);
		
		invoiceFormats = new ArrayList<String>();
		invoiceFormats.add(Model.FORMAT_HOCHFORMAT);
		invoiceFormats.add(Model.FORMAT_QUERFORMAT);
		
		invoiceCreationFrequencies = new ArrayList<String>();
		invoiceCreationFrequencies.add(Model.FREQUENCY_MONTHLY);
		invoiceCreationFrequencies.add(Model.FREQUENCY_QUARTERLY);
		invoiceCreationFrequencies.add(Model.FREQUENCY_HALFYEARLY);
		invoiceCreationFrequencies.add(Model.FREQUENCY_YEARLY);
		
		invoiceColumns = new ArrayList<String>();
//		invoiceColumns.add(COLUMN_POS);
//		invoiceColumns.add(COLUMN_AMOUNT);
		invoiceColumns.add(COLUMN_DESCRIPTION);
		invoiceColumns.add(COLUMN_CARD_NR);
		invoiceColumns.add(COLUMN_TEL_NR);
		invoiceColumns.add(COLUMN_INST_PLZ);
		invoiceColumns.add(COLUMN_EINSATZORT);
		invoiceColumns.add(COLUMN_INST_STREET);
		invoiceColumns.add(COLUMN_PLANT_NUMBER);
		invoiceColumns.add(COLUMN_EQUIP_NR);
		invoiceColumns.add(COLUMN_AUFTRAGS_NR);
		invoiceColumns.add(COLUMN_BESTELL_NR);
		invoiceColumns.add(COLUMN_VERTRAG_NR);
		invoiceColumns.add(COLUMN_BA_NR);
		invoiceColumns.add(COLUMN_WE_NR);
		invoiceColumns.add(COLUMN_COST_CENTER);
//		invoiceColumns.add(COLUMN_SINGLE_PRICE);
		
		columnSize = new HashMap<String, Float>();
		columnSize.put(COLUMN_POS, 1.8f);
		columnSize.put(COLUMN_AMOUNT, 2.5f);
		columnSize.put(COLUMN_DESCRIPTION, 12.5f);
		columnSize.put(COLUMN_CARD_NR, 5f);
		columnSize.put(COLUMN_TEL_NR, 6f);
		columnSize.put(COLUMN_INST_PLZ, 1.5f);
		columnSize.put(COLUMN_EINSATZORT, 5f);
		columnSize.put(COLUMN_INST_STREET, 8f);
		columnSize.put(COLUMN_PLANT_NUMBER, 5f);
		columnSize.put(COLUMN_EQUIP_NR, 5f);
		columnSize.put(COLUMN_AUFTRAGS_NR, 4f);
		columnSize.put(COLUMN_BESTELL_NR, 6f);
		columnSize.put(COLUMN_VERTRAG_NR, 5.3f);
		columnSize.put(COLUMN_BA_NR, 4f);
		columnSize.put(COLUMN_WE_NR, 4f);
		columnSize.put(COLUMN_COST_CENTER, 4f);
//		columnSize.put(COLUMN_SINGLE_PRICE, 4f);
		columnSize.put(COLUMN_TOTAL_PRICE, 3f);

		billingCriteria = new ArrayList<String>();
		billingCriteria.add(BILLING_WE_NR);
		billingCriteria.add(BILLING_BESTELL_NR);
		
		relationList = new ArrayList<String>();
		relationList.add(RELATION_0);
		relationList.add(RELATION_1);
		relationList.add(RELATION_2);
		
		paymentMethods = new ArrayList<String>();
		paymentMethods.add(paymentInvoice);
		paymentMethods.add(paymentDebit);
		
		invoiceRowList = new ArrayList<Integer>();
		invoiceRowList.add(1);
		invoiceRowList.add(2);
		invoiceRowList.add(3);
		invoiceRowList.add(4);
		invoiceRowList.add(5);
		
		months = new HashMap<Integer, String>();
		months.put(0, JAN);
		months.put(1, FEB);
		months.put(2, MAR);
		months.put(3, APR);
		months.put(4, MAY);
		months.put(5, JUN);
		months.put(6, JUL);
		months.put(7, AUG);
		months.put(8, SEP);
		months.put(9, OCT);
		months.put(10, NOV);
		months.put(11, DEC);
		
		years = addYears();
		
		String hostname = "";
		try {
			hostname = java.net.InetAddress.getLocalHost().getHostName();
			LOGGER.info("Hostname = " + hostname);
		} catch (UnknownHostException e) {
			LOGGER.error("UnknownHostnameException: " + e);
		}
		
		if (hostname.equals("accounting")) {
			pdfPath = "E:/tmp/Invoices/pdf/";
			zipPath = "E:/tmp/Invoices/zip/";
		} else {
			pdfPath = "C:/Siwaltec/Invoices/pdf/";
			zipPath = "C:/Siwaltec/Invoices/zip/";
		}

	}

	private List<Integer> addYears() {
		ArrayList<Integer> years = new ArrayList<Integer>();
		int year = 2010;
		Calendar d = Calendar.getInstance();
		
		while (year <= d.get(Calendar.YEAR)) {
			years.add(year);
			year++;
		}
		return years;
	}
	
	public List<SelectItem> getCountryCodes() {
		List<SelectItem> codes = new ArrayList<SelectItem>();
		Iterator<Country> it = countries.iterator();
		while (it.hasNext()) {
			codes.add(new SelectItem(it.next().getInternationalAreaCode()));
		}
		return codes;
	}

	public Country getCountryByShortName(String shortName) {
		Iterator<Country> it = countries.iterator();
		while (it.hasNext()) {
			Country c = it.next();
			if (c.getShortName().equals(shortName))
				return c;
		}
		
		return null;
	}
	
	public List<SelectItem> getCountryNames() {
		List<SelectItem> countryNames = new ArrayList<SelectItem>();
		Iterator<Country> it = countries.iterator();
		while (it.hasNext()) {
			countryNames.add(new SelectItem(it.next().getShortName()));
		}
		return countryNames;
	}
	
	public List<SelectItem> getGenders() {
		List<SelectItem> genders = new ArrayList<SelectItem>();
		genders.add(new SelectItem(Model.GENDER_MAN));
		genders.add(new SelectItem(Model.GENDER_WOMAN));
		genders.add(new SelectItem(Model.GENDER_COMPANY));
		return genders;
	}
	
	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
	
	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public List<String> getInvoiceFormats() {
		return invoiceFormats;
	}

	public void setInvoiceFormats(List<String> invoiceFormats) {
		this.invoiceFormats = invoiceFormats;
	}

	public List<String> getInvoiceCreationFrequencies() {
		return invoiceCreationFrequencies;
	}

	public void setInvoiceCreationFrequencies(
			List<String> invoiceCreationFrequencies) {
		this.invoiceCreationFrequencies = invoiceCreationFrequencies;
	}

	public Map<Integer, Double> getSimPrices() {
		if (simPrices == null || (simPrices != null && simPrices.size() == 0) || (FacesUtil.getAttributeFromRequest("updateSimPrices") != null)) {
			ConfigurationController c = new ConfigurationController();
			simPrices = c.getSimPricesFromDB();
		}
		return simPrices;
	}
	
	public Map<Integer, Double> getDataOptionSurcharges() {
		if (dataOptionSurcharges == null || (dataOptionSurcharges != null && dataOptionSurcharges.size() == 0)  || (FacesUtil.getAttributeFromRequest("updateDataOptions") != null)) {
			ConfigurationController c = new ConfigurationController();
			dataOptionSurcharges = c.getDataOptionPricesFromDB();
		}
		return dataOptionSurcharges;
	}

	public List<String> getInvoiceColumns() {
		return invoiceColumns;
	}

	public void setInvoiceColumns(List<String> invoiceColumns) {
		this.invoiceColumns = invoiceColumns;
	}

	public HashMap<String, Float> getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(HashMap<String, Float> columnSize) {
		this.columnSize = columnSize;
	}

	public List<String> getSupplierList() {
		return supplierList;
	}

	public void setSupplierList(List<String> supplierList) {
		this.supplierList = supplierList;
	}

	public String getCardDeAv() {
		return cardDeAv;
	}

	public String getCardDeAvIp() {
		return cardDeAvIp;
	}

	public String getCardDeM2m() {
		return cardDeM2m;
	}

	public String getCardDeHbn() {
		return cardDeHbn;
	}

	public String getCardDeTc() {
		return cardDeTc;
	}

	public String getCardDeSc() {
		return cardDeSc;
	}

	public String getCardDeRc() {
		return cardDeRc;
	}

	public String getCardDeAvCmt() {
		return cardDeAvCmt;
	}

	public String getCardDeAvIpCmt() {
		return cardDeAvIpCmt;
	}

	public String getCardDeM2mCmt() {
		return cardDeM2mCmt;
	}

	public String getCardDeHbnCmt() {
		return cardDeHbnCmt;
	}

	public String getCardDeTcCmt() {
		return cardDeTcCmt;
	}

	public String getCardDeScCmt() {
		return cardDeScCmt;
	}

	public String getCardDeRcCmt() {
		return cardDeRcCmt;
	}

	public void setCardDeAv(String cardDeAv) {
		this.cardDeAv = cardDeAv;
	}

	public void setCardDeAvCmt(String cardDeAvCmt) {
		this.cardDeAvCmt = cardDeAvCmt;
	}

	public void setCardDeAvIp(String cardDeAvIp) {
		this.cardDeAvIp = cardDeAvIp;
	}

	public void setCardDeM2m(String cardDeM2m) {
		this.cardDeM2m = cardDeM2m;
	}

	public void setCardDeHbn(String cardDeHbn) {
		this.cardDeHbn = cardDeHbn;
	}

	public void setCardDeTc(String cardDeTc) {
		this.cardDeTc = cardDeTc;
	}

	public void setCardDeSc(String cardDeSc) {
		this.cardDeSc = cardDeSc;
	}

	public void setCardDeRc(String cardDeRc) {
		this.cardDeRc = cardDeRc;
	}

	public void setCardDeAvIpCmt(String cardDeAvIpCmt) {
		this.cardDeAvIpCmt = cardDeAvIpCmt;
	}

	public void setCardDeM2mCmt(String cardDeM2mCmt) {
		this.cardDeM2mCmt = cardDeM2mCmt;
	}

	public void setCardDeHbnCmt(String cardDeHbnCmt) {
		this.cardDeHbnCmt = cardDeHbnCmt;
	}

	public void setCardDeTcCmt(String cardDeTcCmt) {
		this.cardDeTcCmt = cardDeTcCmt;
	}

	public void setCardDeScCmt(String cardDeScCmt) {
		this.cardDeScCmt = cardDeScCmt;
	}

	public void setCardDeRcCmt(String cardDeRcCmt) {
		this.cardDeRcCmt = cardDeRcCmt;
	}

	public String getCardDeEmptyCmt() {
		return cardDeEmptyCmt;
	}

	public void setCardDeEmptyCmt(String cardDeEmptyCmt) {
		this.cardDeEmptyCmt = cardDeEmptyCmt;
	}

	public String getCardAut1() {
		return cardAut1;
	}

	public String getCardAut2() {
		return cardAut2;
	}

	public String getCardAut3() {
		return cardAut3;
	}

	public String getCardAut4() {
		return cardAut4;
	}

	public String getCardAut5() {
		return cardAut5;
	}

	public String getCardAut6() {
		return cardAut6;
	}

	public String getCardAut7() {
		return cardAut7;
	}

	public String getCardAut8() {
		return cardAut8;
	}

	public String getCardAut9() {
		return cardAut9;
	}

	public String getCardAutAA1() {
		return cardAutAA1;
	}

	public String getCardAutAA2() {
		return cardAutAA2;
	}

	public List<String> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<String> relationList) {
		this.relationList = relationList;
	}

	public List<String> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<String> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public String getPaymentInvoice() {
		return paymentInvoice;
	}

	public void setPaymentInvoice(String paymentInvoice) {
		this.paymentInvoice = paymentInvoice;
	}

	public String getPaymentDebit() {
		return paymentDebit;
	}

	public void setPaymentDebit(String paymentDebit) {
		this.paymentDebit = paymentDebit;
	}

	public List<Integer> getInvoiceRowList() {
		return invoiceRowList;
	}

	public void setInvoiceRowList(List<Integer> invoiceRowList) {
		this.invoiceRowList = invoiceRowList;
	}

	public List<String> getBillingCriteria() {
		return billingCriteria;
	}

	public void setBillingCriteria(List<String> billingCriteria) {
		this.billingCriteria = billingCriteria;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public HashMap<Integer, String> getMonths() {
		return months;
	}

	public void setMonths(HashMap<Integer, String> months) {
		this.months = months;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getZipPath() {
		return zipPath;
	}

	public void setZipPath(String zipPath) {
		this.zipPath = zipPath;
	}

}