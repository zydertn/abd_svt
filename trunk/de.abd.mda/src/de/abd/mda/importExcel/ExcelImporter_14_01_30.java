package de.abd.mda.importExcel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.abd.mda.controller.CardActionController;
import de.abd.mda.model.Country;
import de.abd.mda.model.Model;
import de.abd.mda.persistence.dao.Address;
import de.abd.mda.persistence.dao.CardBean;
import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;
import de.abd.mda.persistence.dao.Person;
import de.abd.mda.persistence.dao.SequenceNumber;
import de.abd.mda.persistence.dao.controller.CardController;
import de.abd.mda.persistence.dao.controller.CustomerController;
import de.abd.mda.persistence.hibernate.SessionFactoryUtil;
import de.abd.mda.util.MdaLogger;

import jxl.demo.CSV;

public class ExcelImporter_14_01_30 {

	/**
	 * @param args
	 */
		private String zeile;
		private ArrayList<CardBean> list = new ArrayList<CardBean>();
		private String[] split = null;
		static final Logger logger = Logger.getLogger(ExcelImporter_14_01_30.class);
			
		public static void main(String[] args) {
			ExcelImporter_14_01_30 c = new ExcelImporter_14_01_30();
			c.readData();
		}
		
		public void readData() {
			try {
					String hostname = java.net.InetAddress.getLocalHost().getHostName();
					System.out.println("Hostname" + hostname);
					
					String path = "E:/ExcelImport/Rollout/";
					
					if (hostname.equals("STR00108")) {
						path = "D:/Softwareentwicklung/Excel-Lieferung-Sina/Rollout/2014_01_30/";
					}
					
					FileReader file = null;
	
//					logger.info("************ 20011_GSM-Tel._Karten_Thyssen_K�ln-H�rthl.csv **************");
//					file = new FileReader(path + "20011_GSM-Tel._Karten_Thyssen_K�ln-H�rthl.csv");
//					readDataFromFile(file, "20011");
//					writeOutList(list);
					logger.info("************ 20051_GSM-Tel._Fa._Thyssen_Mannheim.csv **************");
					file = new FileReader(path + "20051_GSM-Tel._Fa._Thyssen_Mannheim.csv");
					readDataFromFile(file, "20051");
					writeOutList(list);
					
//					logger.info("************ 20067_QUARTAL_GSM-Tel._Fa._Thyssen_Hannover.csv **************");
//					file = new FileReader(path + "20067_QUARTAL_GSM-Tel._Fa._Thyssen_Hannover.csv");
//					readDataFromFile(file, "20067");
//					writeOutList(list);
//
//					logger.info("************ 20109_HALBJAHR_GSM-Tel._Fa._ThyssenKrupp_GmbH_NL_KA.csv **************");
//					file = new FileReader(path + "20109_HALBJAHR_GSM-Tel._Fa._ThyssenKrupp_GmbH_NL_KA.csv");
//					readDataFromFile(file, "20109");
//					writeOutList(list);
//
//					logger.info("************ 20113_GSM-SIM_Karten_TKA_NL_Leipzig.csv **************");
//					file = new FileReader(path + "20113_GSM-SIM_Karten_TKA_NL_Leipzig.csv");
//					readDataFromFile(file, "20113");
//					writeOutList(list);
//
//					logger.info("************ 20117_GSM-SIM_Karten_ThyssenKrupp_NL_Kiel.csv **************");
//					file = new FileReader(path + "20117_GSM-SIM_Karten_ThyssenKrupp_NL_Kiel.csv");
//					readDataFromFile(file, "20117");
//					writeOutList(list);
//
//					logger.info("************ 20118_GSM-SIM_Karten_ThyssenKrupp_NL_Magdeburg.csv **************");
//					file = new FileReader(path + "20118_GSM-SIM_Karten_ThyssenKrupp_NL_Magdeburg.csv");
//					readDataFromFile(file, "20118");
//					writeOutList(list);
//
//					logger.info("************ 20125_JAHR_GSM-Tel._Karten_ThyssenKrupp_NL_Heilbronn.csv **************");
//					file = new FileReader(path + "20125_JAHR_GSM-Tel._Karten_ThyssenKrupp_NL_Heilbronn.csv");
//					readDataFromFile(file, "20125");
//					writeOutList(list);
//
//					logger.info("************ 20152_GSM-SIM_Karten_ThyssenKrupp_NL_Mainz.csv **************");
//					file = new FileReader(path + "20152_GSM-SIM_Karten_ThyssenKrupp_NL_Mainz.csv");
//					readDataFromFile(file, "20152");
//					writeOutList(list);
//
//					logger.info("************ 20226_GSM_TKA_NL_Kassel.csv **************");
//					file = new FileReader(path + "20226_GSM_TKA_NL_Kassel.csv");
//					readDataFromFile(file, "20226");
//					writeOutList(list);
//
//					logger.info("************ 20261_JAHR_GSM-Tel._MBV_Karlsruhe.csv **************");
//					file = new FileReader(path + "20261_JAHR_GSM-Tel._MBV_Karlsruhe.csv");
//					readDataFromFile(file, "20261");
//					writeOutList(list);

//					logger.info("************ 20185_GSM-Tel_Fa._TKA_NL_Bremen.csv **************");
//					file = new FileReader(path + "20185_GSM-Tel_Fa._TKA_NL_Bremen.csv");
//					readDataFromFile(file, "20185");
//					writeOutList(list);
//
//					logger.info("************ 20199_GSM-Tel_Fa._TKA_NL_Freiburg.csv **************");
//					file = new FileReader(path + "20199_GSM-Tel_Fa._TKA_NL_Freiburg.csv");
//					readDataFromFile(file, "20199");
//					writeOutList(list);
//
//					logger.info("************ 20233_GSM-SIM_Karten_ThyssenKrupp_NL_Bayreuth.csv **************");
//					file = new FileReader(path + "20233_GSM-SIM_Karten_ThyssenKrupp_NL_Bayreuth.csv");
//					readDataFromFile(file, "20233");
//					writeOutList(list);
//
//					                          
//					logger.info("************ 20091_GSM_Liste_FA._Tepper_mit_Vertragsnummern.csv **************");
//					file = new FileReader(path + "20091_GSM_Liste_FA._Tepper_mit_Vertragsnummern.csv");
//					readDataFromFile(file, "20091");
//					writeOutList(list);
//
//					logger.info("************ 20181_GSM-Tel._Fa._GMT_Aufzug-Service_GmbH.csv **************");
//					file = new FileReader(path + "20181_GSM-Tel._Fa._GMT_Aufzug-Service_GmbH.csv");
//					readDataFromFile(file, "20181");
//					writeOutList(list);
//
//					logger.info("************ 20047_GSM-Tel_Fa_Liftservice_u_Montage_GmbH_29.03.2012.csv **************");
//					file = new FileReader(path + "20047_GSM-Tel_Fa_Liftservice_u_Montage_GmbH_29.03.2012.csv");
//					readDataFromFile(file, "20047");
//					writeOutList(list);

			} catch (FileNotFoundException e) {
				logger.error("Datei nicht gefunden");
			} catch (IOException e) {
				logger.error("E/A-Fehler");
			}
			
	}

		
	private void writeOutList(ArrayList<CardBean> list2) {
			logger.info("Anzahl S�tze: " + list.size());
			for (CardBean c : list2) {
				logger.info("Importierte Karte: " + c.getCardNumberFirst() + " " + c. getCardNumberSecond());
			}
				
			list = new ArrayList<CardBean>();
		}

	public void readDataFromFile(FileReader file, String customerNumber) throws IOException {
		BufferedReader data = new BufferedReader(file);
		zeile = data.readLine();
		zeile = data.readLine();
		split = zeile.split(";");
		int length = split.length;
		HashMap<String, Integer> spalten = new HashMap<String, Integer>();
		for (int i = 0; i < length; i++) {
			spalten.put(split[i], i);
		}
		
		
		while ((zeile = data.readLine()) != null) {
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			split = zeile.split(";");
			CardBean card = new CardBean();
			CardBean existingCard = null;
			if (split.length > 0) {
				logger.debug("---------------------------------------------------------------");
				logger.debug("split[1] == " + split[1]);
				card = extractCardNums(card, split[1]);
//				card.setCardNumberFirst(extractCardNum(split[0]));

				try {
					String select = "select distinct card from CardBean card where card.cardNumberFirst = '" + card.getCardNumberFirst() + "'";
					if (card.getCardNumberSecond() != null && card.getCardNumberSecond().length() > 0 && !card.getCardNumberSecond().equals(" ")) {
						select = select + "	and card.cardNumberSecond = '" + card.getCardNumberSecond() +"'";
					}
					tx = session.beginTransaction();
					List<CardBean> list = session.createQuery(select).list();
					
					if (list.size() > 0) {
						if (list.size() > 1) {
							MdaLogger.warn(logger, "Mehr als eine Karte gefunden!");
							continue;
						} else {
							MdaLogger.info(logger, "Nur eine Karte gefunden!");
							existingCard = (CardBean) list.get(0);
						}
					} else {
						MdaLogger.info(logger, "Keine Karte gefunden!");
					}
				} catch (Exception e) {
					MdaLogger.error(logger, e);
				}
			}

			Customer customer = null;
			try {
				customer = (Customer) searchCustomer(customerNumber, null).get(0);
			} catch (Exception e) {
				MdaLogger.error(logger, "Kunde existiert nicht!!");
				break;
			}
			
			if (existingCard != null) {
				setCardValues(existingCard, split, spalten, customer);
//				// Deutsche Karten
//				existingCard.setSupplier(Model.SUPPLIER_TELEKOM);
				list.add(existingCard);
			} else {
				setCardValues(card, split, spalten, customer);
//				// Deutsche Karten
//				card.setSupplier(Model.SUPPLIER_TELEKOM);
				CardController cardController = new CardController();
				String retMessage = cardController.createObject(card);
				if (retMessage != null && retMessage.length() == 0) {
					MdaLogger.info(logger, retMessage);
				}
				list.add(card);
			}
			
			try {
				tx.commit();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}


		}
	}
	
	private void setCardValues(CardBean card, String[] split2,
			HashMap<String, Integer> spalten, Customer customer) {
		if (spalten.containsKey("Rufnummer")) {
			if (card.getCardNumberFirst().equals("78702559")) {
				System.out.println("Jetzt");
			}
			extractTelNums(card, split[spalten.get("Rufnummer")]);
		}
		if (spalten.containsKey("Telefon Nr.")) {
			extractTelNums(card, split[spalten.get("Telefon Nr.")]);
		}
		if (spalten.containsKey("NR.Telefon")) {
			extractTelNums(card, split[spalten.get("NR.Telefon")]);
		}
		if (spalten.containsKey("Tel. Nummer")) {
			extractTelNums(card, split[spalten.get("Tel. Nummer")]);
		}
		if (spalten.containsKey("PLZ")) {
			Address instAdd = getCardAddress(card);
			instAdd.setPostcode(""+split[spalten.get("PLZ")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Einsatzort")) {
			Address instAdd = getCardAddress(card);
			instAdd.setCity(""+split[spalten.get("Einsatzort")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Ort")) {
			Address instAdd = getCardAddress(card);
			instAdd.setCity(""+split[spalten.get("Ort")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Stra�e")) {
			Address instAdd = getCardAddress(card);
			instAdd.setStreet(""+split[spalten.get("Stra�e")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Strasse")) {
			Address instAdd = getCardAddress(card);
			instAdd.setStreet(""+split[spalten.get("Strasse")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Anschrift")) {
			Address instAdd = getCardAddress(card);
			instAdd.setStreet(""+split[spalten.get("Anschrift")]);
			card.setInstallAddress(instAdd);
		}
		if (spalten.containsKey("Aufzug Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Aufzug Nr.")]);
		}
		if (spalten.containsKey("Anlagen Nr.")) {
			card.setFactoryNumber(split[spalten.get("Anlagen Nr.")]);
		}
		if (spalten.containsKey("Aufzug-Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Aufzug-Nr.")]);
		}
		if (spalten.containsKey("Fab. Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Fab. Nr.")]);
		}
		if (spalten.containsKey("Fab. Nr")) {
			card.setFactoryNumber(""+split[spalten.get("Fab. Nr")]);
		}
		if (spalten.containsKey("Fabr. Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Fabr. Nr.")]);
		}
		if (spalten.containsKey("Fabrik Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Fabrik Nr.")]);
		}
		if (spalten.containsKey("Fabrik. Nr.")) {
			card.setFactoryNumber(""+split[spalten.get("Fabrik. Nr.")]);
		}
		if (spalten.containsKey("Leitstand")) {
			card.setLeitstand(""+split[spalten.get("Leitstand")]);
		}
		if (spalten.containsKey("Leitst. Nr.")) {
			card.setLeitstand(""+split[spalten.get("Leitst. Nr.")]);
		}
		if (spalten.containsKey("zugel. Notruf Nr.")) {
			card.setLeitstand(""+split[spalten.get("zugel. Notruf Nr.")]);
		}
		if (spalten.containsKey("Lokation")) {
			card.setLokation(""+split[spalten.get("Lokation")]);
		}
		if (spalten.containsKey("Meinsatzort")) {
			card.setEinsatzort(""+split[spalten.get("Meinsatzort")]);
		}
		if (spalten.containsKey("Status")) {
			card.setStatus(""+split[spalten.get("Status")]);
		}
		if (spalten.containsKey("BA Nr.")) {
			card.setBaNummer(""+split[spalten.get("BA Nr.")]);
		}
		if (spalten.containsKey("BA - Nr.")) {
			card.setBaNummer(""+split[spalten.get("BA - Nr.")]);
		}

		String date = null;

		if (spalten.containsKey("Datum")) {
			date = split[spalten.get("Datum")];
		} else if (spalten.containsKey("Aktiv. Datum")) {
			date = split[spalten.get("Aktiv. Datum")];
		}
		
		if (date != null && date.length() > 1) {
			System.out.println(date);
			Calendar c = new GregorianCalendar();
			try{
				c.set(new Integer(date.substring(6, 10)), new Integer(date.substring(3, 5)) - 1, new Integer(date.substring(0, 2)));
			} catch (Exception e) {
				System.out.println(e);
			}
			if (card.getStatus().equalsIgnoreCase(Model.STATUS_ACTIVE)) {
				card.setActivationDate(c.getTime());
			} else if (card.getStatus().equalsIgnoreCase(Model.STATUS_INACTIVE) || card.getStatus().equals(Model.STATUS_DUMMY)) {
				card.setDeactivationDate(c.getTime());
			}
		}

		
		if (spalten.containsKey("Auftrag Nr.")) {
			card.setAuftragsNr(""+split[spalten.get("Auftrag Nr.")]);
		}
		if (spalten.containsKey("Auftragsnummer")) {
			if (split[spalten.get("Auftragsnummer")] != null)
				card.setAuftragsNr(""+split[spalten.get("Auftragsnummer")]);
		}
		if (spalten.containsKey("Auftragsnr.")) {
			if (split[spalten.get("Auftragsnr.")] != null)
				card.setAuftragsNr(""+split[spalten.get("Auftragsnr.")]);
		}
		if (spalten.containsKey("Vertragsnummer")) {
			card.setVertrag(""+split[spalten.get("Vertragsnummer")]);
		}
		if (spalten.containsKey("Vertrag. Nr.")) {
			card.setVertrag(""+split[spalten.get("Vertrag. Nr.")]);
		}
		if (spalten.containsKey("Vertrag Nr.")) {
			card.setVertrag(""+split[spalten.get("Vertrag Nr.")]);
		}
		if (spalten.containsKey("Vertr. Nr.")) {
			card.setVertrag(""+split[spalten.get("Vertr. Nr.")]);
		}
		if (spalten.containsKey("Vertrags Nr.")) {
			card.setVertrag(""+split[spalten.get("Vertrags Nr.")]);
		}
		if (spalten.containsKey("Vertrag")) {
			card.setVertrag(""+split[spalten.get("Vertrag")]);
		}
		if (spalten.containsKey("Bemerkung")) {
			card.setComment(split[spalten.get("Bemerkung")]);
		}
		if (spalten.containsKey("Bemerkungen")) {
			if (split2.length > spalten.get("Bemerkungen")) {
				String comment = "" + split[spalten.get("Bemerkungen")]; 
				card.setComment(comment);
			}
		}
		if (spalten.containsKey("Memo")) {
			card.setComment(split[spalten.get("Memo")]);
		}
		if (spalten.containsKey("Endkunde")) {
			card.setComment(split[spalten.get("Endkunde")]);
		}
		// pr�fen, ob BestellNummer korrekt ist, oder BestellNummer (auf Karte)
		if (spalten.containsKey("Bestellnr.")) {
			card.setBestellNummer(split[spalten.get("Bestellnr.")]);
		}
		if (spalten.containsKey("Bestellnummer")) {
			card.setBestellNummer(split[spalten.get("Bestellnummer")]);
		}
		if (spalten.containsKey("Bestell Nr.")) {
			card.setBestellNummer(split[spalten.get("Bestell Nr.")]);
		}
		if (spalten.containsKey("Best. Nummer")) {
			card.setBestellNummer(split[spalten.get("Best. Nummer")]);
		}
		if (spalten.containsKey("Kartenpreis")) {
			if (split.length > spalten.get("Kartenpreis") && split[spalten.get("Kartenpreis")] != null) {
				String preis = split[spalten.get("Kartenpreis")];
				int simPrice = Integer.parseInt(preis);
				card.setStandardPrice(false);
				card.setSimPrice(simPrice);
			}
		}

		if (spalten.containsKey("PIN-Code")) {
			String pinCode = split[spalten.get("PIN-Code")];
			if (pinCode != "" && !pinCode.equalsIgnoreCase("Frei") && pinCode.length() > 1) {
				card.setPin(split[spalten.get("PIN-Code")]);
			}
		}
		
		if (spalten.containsKey("Land")) {
			String land = split[spalten.get("Land")];
			if (land != null && land.equals("A")) {
				card.setSupplier(Model.SUPPLIER_TELEKOM_AUSTRIA);
			} else {
				card.setSupplier(Model.SUPPLIER_TELEKOM);
			}
		}
		
		if (spalten.containsKey("Eqiupment Nr.")) {
			card.setEquipmentNr(split[spalten.get("Eqiupment Nr.")]);
		}
		if (spalten.containsKey("SIE IAO Equipment")) {
			card.setEquipmentNr(split[spalten.get("SIE IAO Equipment")]);
		}
		if (spalten.containsKey("Kst.")) {
			card.setKostenstelle(split[spalten.get("Kst.")]);
		}
		
		card.setCustomer(customer);
//		if (spalten.containsKey("Aufzug Nr.")) {
//			card.setAuf)(split[spalten.get("Aufzug Nr.")]);
//		}
		
		
	}

	private Address getCardAddress(CardBean card) {
		Address instAdd = card.getInstallAddress();
		if (instAdd == null) {
			instAdd = new Address();
		}
		return instAdd;
	}

	private CardBean extractCardNums(CardBean card, String string) {
		string = string.replaceAll("\\s", "");
		card.setCardNumberFirst(string.substring(0, string.indexOf("-")));
		card.setCardNumberSecond(string.substring(string.indexOf("-")+1));
		return card;
	}

	private CardBean extractTelNums(CardBean card, String string) {
		if (string != null && string.length() > 0) {
			string = string.replaceAll("\\s", "");
			card.setPhoneNrFirst(string.substring(0, string.indexOf("-")));
			card.setPhoneNrSecond(string.substring(string.indexOf("-")+1));
		}
		return card;
	}
	
	private List<DaoObject> searchCustomer (String customerNumber, String customerName) {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		List<DaoObject> customers = null;
		tx = session.beginTransaction();
		String whereClause = "";
		if (customerNumber != null && customerNumber.length() > 0) {
			whereClause = " where customer.customernumber = '" + customerNumber + "'";
			if (customerName != null && customerName.length() > 0) {
				whereClause += " && customer.name = '" + customerName + "'";
			}
		} else if (customerName != null && customerName.length() > 0) {
			whereClause += " where customer.name = '" + customerName + "'";
		}
		
		customers = session.createQuery("from Customer as customer" + whereClause).list();

		return customers;
}

}