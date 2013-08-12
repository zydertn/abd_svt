package de.abd.mda.importExcel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.abd.mda.model.Country;
import de.abd.mda.model.Model;
import de.abd.mda.persistence.dao.Address;
import de.abd.mda.persistence.dao.CardBean;
import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;
import de.abd.mda.persistence.dao.Person;
import de.abd.mda.persistence.dao.SequenceNumber;
import de.abd.mda.persistence.dao.controller.CardController;
import de.abd.mda.persistence.hibernate.SessionFactoryUtil;
import de.abd.mda.util.MdaLogger;

public class ExcelCorrection_13_08_01 {

	/**
	 * @param args
	 */
	private String zeile;
	private ArrayList list = new ArrayList();
	private String[] split = null;
	static final Logger logger = Logger
			.getLogger(ExcelCorrection_13_08_01.class);

	public static void main(String[] args) {
		ExcelCorrection_13_08_01 c = new ExcelCorrection_13_08_01();
		try {
//			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_STATUS.csv"), "Status");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_DEL_SLIP_DATE.csv"), "delSlipDate");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_FAC_NUMBER.csv"), "facNumber");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_INST_ADD_CITY.csv"), "instAddCity");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_INST_ADD_HNUMBER.csv"), "instAddHNumber");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_INST_ADD_POSTCODE.csv"), "instAddPostcode");
			c.updateColumn(c.readData("D:/Softwareentwicklung/Excel-Lieferung-Sina/Datenbank_Korrekturen_01_08_2013_INST_ADD_STREET.csv"), "instAddStreet");

		} catch (IOException e) {
			logger.error("E/A-Fehler");
		}
	}

	public FileReader readData(String path) {
		FileReader file = null;
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e) {
			logger.error("Datei nicht gefunden");
		}
				
		return file;
	}

	public void updateColumn(FileReader file, String fall) throws IOException {
		BufferedReader data = new BufferedReader(file);
		data.readLine();
		int i = 0;
		while ((zeile = data.readLine()) != null) {
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance()
					.getCurrentSession();
			split = zeile.split(";");
			CardBean card = new CardBean();
			CardBean existingCard = null;
			if (split.length > 0) {
				logger.debug("---------------------------------------------------------------");
				logger.debug("split[0] == " + split[0]);
				card.setCardNumberFirst(split[0]);
			}
			if (split.length > 1) {
				logger.debug("---------------------------------------------------------------");
				logger.debug("split[1] == " + split[1]);
				card.setCardNumberSecond(split[1]);
			}

			if (split.length > 2) {
				logger.debug("---------------------------------------------------------------");
				logger.debug("split[2] == " + split[2]);

				Address instAdd = card.getInstallAddress();
				if (fall.equals("status")) {
					card.setStatus(split[2]);
				} else if (fall.equals("delSlipDate")) {
					String date = split[2];
					if (date != null && date.length() > 0) {
						Calendar c = new GregorianCalendar();
						c.set(new Integer(date.substring(6, 10)), new Integer(
								date.substring(3, 5)) - 1,
								new Integer(date.substring(0, 2)));
						card.setDeliverySlipDate(c.getTime());
					} else if (fall.equals("facNumber")) {
						card.setFactoryNumber(split[2]);
					} else if (fall.equals("instAddCity")) {
						instAdd.setCity(split[2]);
					} else if (fall.equals("instAddHNumber")) {
						instAdd.setHousenumber(split[2]);
					} else if (fall.equals("instAddPostcode")) {
						instAdd.setPostcode(split[2]);
					} else if (fall.equals("instAddStreet")) {
						instAdd.setStreet(split[2]);
					} else {
						logger.error("Irgendwas ging schief! Fall = " + fall);
					}

				}
			}
		}
	}


}