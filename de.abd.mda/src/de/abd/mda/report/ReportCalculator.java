package de.abd.mda.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.icesoft.faces.context.Resource;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import de.abd.mda.model.Model;
import de.abd.mda.persistence.dao.Bill;
import de.abd.mda.persistence.dao.CardBean;
import de.abd.mda.persistence.dao.Configuration;
import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;
import de.abd.mda.persistence.dao.controller.BillController;
import de.abd.mda.persistence.dao.controller.CustomerController;
import de.abd.mda.persistence.hibernate.SessionFactoryUtil;
import de.abd.mda.util.CardComparator;
import de.abd.mda.util.CustomerComparator;
import de.abd.mda.util.DateUtils;
import de.abd.mda.util.FacesUtil;

public class ReportCalculator implements Runnable {
	static final Logger logger = Logger.getLogger(ReportCalculator.class);
	HttpSession facesSession;
	private boolean taskRunning = true;
	Thread thread;
		
	public static final Resource ZIP_RESOURCE = new MyResource("Invoices/Siwaltec_Rechnungen.zip");
	
	public ReportCalculator() {
	}

	public boolean calculate() {
		if (facesSession != null
				&& facesSession.getAttribute("reportProgress") != null) {
			System.out.println("Reporterstellung l�uft bereits!");
			return false;
		}
		if (facesSession != null)
			facesSession.setAttribute("reportProgress", 0);

		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String select = "from Configuration";

		List<Configuration> list = session.createQuery(select).list();
		Configuration c = null;
		if (list.size() > 0) {
			c = list.get(0);
		} else {
			c = new Configuration();
		}

		c.setReportProgress(0);
		tx.commit();

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		// now.set(Calendar.YEAR, 2014);
		// now.set(Calendar.MONTH, 1);
		// now.set(Calendar.DAY_OF_MONTH, 5);
		// Um den Fehlerfall auszuschlie�en, wenn jemand exakt am 1. eines
		// Monats um 0 Uhr auf den Knopf dr�ckt (damit dieser Monat auch
		// berechnet wird):
		now.set(Calendar.SECOND, 1);
		Calendar frequencyDate = Calendar.getInstance();
		frequencyDate.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1,
				0, 0, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.out.println(sdf.format(frequencyDate.getTime()));
		logger.info("Reporterstellungslauf gestartet, "
				+ sdf.format(now.getTime()));

		/***** 1. Alle Kunden ermitteln *****/
		List<DaoObject> customers = searchCustomers();
		if (customers != null) {
			logger.info("Anzahl gefundene Kunden = " + customers.size());
		} else {
			return false;
		}

		/***** 2. F�r jeden Kunden die Karten ermitteln *****/
		Iterator<DaoObject> cusIt = customers.iterator();
		int i = 0;
		int cusNum = 0;
		boolean flatrateCalc = false;
		
		while (cusIt.hasNext()) {
			cusNum++;
			Calendar calcMonth = Calendar.getInstance();
			calcMonth.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
			calcMonth.set(Calendar.MILLISECOND, 0);

			// System.out.println(calcMonth.get(Calendar.MONTH));

			Customer customer = (Customer) cusIt.next();
			logger.info("Aktueller Kunde: " + customer.getListString()
					+ ", Kundennummer: " + customer.getCustomernumber());

			Calendar maxCalcDate = getMaxCalcDate(customer
					.getInvoiceConfiguration().getCreationFrequency(), now);

			if (customer.getInvoiceConfiguration().getCreationFrequency()
					.equals(Model.FREQUENCY_YEARLY)) {
				System.out
						.println("YEARLY CUSTOMER ****************; SPEZIALBEHANDLUNG *************");
				System.out.println("Customer: " + customer.getCustomernumber()
						+ ", " + customer.getName());
			}

			while (calcMonth.before(maxCalcDate)) {
				session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = createTransaction(session);

				List<DaoObject> customerCards = searchCards(customer,
						calcMonth, tx, session, flatrateCalc);

				if (customerCards != null && customerCards.size() > 0) {
					i++;

					HashMap<Integer, List<DaoObject>> separateBillingSortedCards = sortCards(
							customer, customerCards);

					int mapCountSize = separateBillingSortedCards.keySet().size();
					int mapCount = 1;
					for (List<DaoObject> cusCards : separateBillingSortedCards.values()) {
						Comparator<DaoObject> comparator = new CardComparator();
						Collections.sort(cusCards, comparator);

						long time1 = System.currentTimeMillis();
						boolean generatedWithoutErrors = generateReport(customer, cusCards, calcMonth, false, (mapCountSize > 1), mapCount);
						long time2 = System.currentTimeMillis();
						long diff = time2-time1;
						System.out.println("generateReport Dauer = " + diff);
						logger.info("GenerateReport Dauer = " + diff);
						if (!generatedWithoutErrors)
							break;

						if (customer.getCustomernumber().equals("20243")) {
							// Kunde OTIS - braucht auch eine Flatrate-Rechnung
							customerCards = searchCards(customer, calcMonth, tx, session, true);
							generatedWithoutErrors = generateReport(customer, cusCards, calcMonth, true, false, 1);
							if (!generatedWithoutErrors)
								break;
						}
						
						mapCount++;
					}
				}

				tx.commit();
				calcMonth = raiseMonth(calcMonth, customer);
				String dfs = "yyyy-MM-dd_HH-mm-ss";
				SimpleDateFormat sd = new SimpleDateFormat(dfs);

				System.out.println("CalcMonth: "
						+ sd.format(calcMonth.getTime()));
				System.out.println("maxCalcDate: "
						+ sd.format(maxCalcDate.getTime()));
				System.out
						.println("-------------------------------------------------");
			}

			Double prog = (new Double(cusNum) / new Double(customers.size())) * 100.0;
			int progress = prog.intValue();
			if (facesSession != null)
				facesSession.setAttribute("reportProgress", prog.intValue());

			session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			list = null;
			list = session.createQuery(select).list();
			c = null;
			if (list.size() > 0) {
				c = list.get(0);
			} else {
				c = new Configuration();
			}

			c.setReportProgress(prog.intValue());
			c.setCustomer(new Integer(customer.getCustomernumber()));
			c.setLastReportUpdate(System.currentTimeMillis());
			tx.commit();
		}

		System.out.println(i + " Reports wurden erstellt!");
		if (facesSession != null)
			facesSession.removeAttribute("reportProgress");

		return true;
	}

	/*
	 * Alle Karten, die gleiche Rechnungskriterien haben, in einer gemeinsamen Liste erfassen
	 * Key = Summe der Hashwerte der Strings der Rechnungskriterien
	 */
	private HashMap<Integer, List<DaoObject>> sortCards(Customer customer,
			List<DaoObject> customerCards) {
		long time1 = System.currentTimeMillis();
		HashMap<Integer, List<DaoObject>> cusCardsSeparateBilling = new HashMap<Integer, List<DaoObject>>();
		if (customer.getInvoiceConfiguration().getSeparateBilling() != null && customer.getInvoiceConfiguration().getSeparateBilling()) {
			String[] sbCrit = customer.getInvoiceConfiguration()
					.getSeparateBillingCriteria();

			for (DaoObject dao : customerCards) {
				CardBean card = (CardBean) dao;
				int hashVal = 0;

				for (int ind = 0; ind < sbCrit.length; ind++) {
					String sbc = sbCrit[ind];
					if (sbc.equals(Model.BILLING_WE_NR)) {
						hashVal += card.getWe().hashCode();
					} else if (sbc.equals(Model.BILLING_BESTELL_NR)) {
						hashVal += card.getBestellNummer().hashCode();
					}
				}

				if (cusCardsSeparateBilling.containsKey(hashVal)) {
					cusCardsSeparateBilling.get(hashVal).add(card);
				} else {
					List cards = new ArrayList<DaoObject>();
					cards.add(card);
					cusCardsSeparateBilling.put(hashVal, cards);
				}

			}
		} else {
			
			cusCardsSeparateBilling.put(0, customerCards);
		}

		long time2 = System.currentTimeMillis();
		long diff = time2-time1;
		System.out.println("Sort Dauer: " + diff + " Millisekunden");
		return cusCardsSeparateBilling;
	}

	Calendar maxCalcDate = Calendar.getInstance();

	private Calendar getMaxCalcDate(String creationFrequency, Calendar now) {
		maxCalcDate.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1, 0,
				0, 0);
		maxCalcDate.set(Calendar.MILLISECOND, 0);

		if (creationFrequency.equals(Model.FREQUENCY_MONTHLY)) {
			// Nothing to be done - Date set to first day of current month
		} else if (creationFrequency.equals(Model.FREQUENCY_QUARTERLY)) {
			if (now.get(Calendar.MONTH) < Calendar.APRIL)
				maxCalcDate.set(Calendar.MONTH, Calendar.JANUARY);
			else if (now.get(Calendar.MONTH) < Calendar.JULY)
				maxCalcDate.set(Calendar.MONTH, Calendar.APRIL);
			else if (now.get(Calendar.MONTH) < Calendar.OCTOBER)
				maxCalcDate.set(Calendar.MONTH, Calendar.JULY);
			else
				maxCalcDate.set(Calendar.MONTH, Calendar.OCTOBER);
		} else if (creationFrequency.equals(Model.FREQUENCY_HALFYEARLY)) {
			if (now.get(Calendar.MONTH) < Calendar.JULY)
				maxCalcDate.set(Calendar.MONTH, Calendar.JANUARY);
			else
				maxCalcDate.set(Calendar.MONTH, Calendar.JULY);
		} else {
			/*
			 * J�hrliche Rechnung darf jederzeit im Jahr erstellt werden; Je
			 * nach Aktivierungsdatum wird eine bestimmte Anzahl Monate
			 * berechnet; Also z.B. 11 Monate f�r dieses Jahr, wenn die Karte im
			 * Februar dieses Jahres aktiviert wurde. Eine f�r das Jahr bereits
			 * in Rechnung gestellte Karte darf nicht erneut in Rechnung
			 * gestellt werden bei sp�terer erneuter
			 * Jahresabrechnungs-Erstellung. Pro Monat soll dabei eine Rechnung
			 * erstellt werden. Handling bzgl. maxCalcDate ist daher wie bei
			 * monatlicher Rechnungsstellung. Es muss also nichts getan werden.
			 */
		}

		return maxCalcDate;
	}

	private boolean generateReport(Customer customer,
			List<DaoObject> customerCards, Calendar calcMonth,
			boolean flatrateCalc, boolean severalBills, int mapCount) {
		IReportGenerator rp = null;
		rp = new ReportGenerator_portrait();

		long time1 = System.currentTimeMillis();
		boolean generatedWithoutError = rp.generateReport(customerCards,
				customer, calcMonth, flatrateCalc, severalBills, mapCount);
		long time2 = System.currentTimeMillis();
		long diff = time2 - time1;
		System.out.println("Inner generateReport Dauer = "+ diff);
		logger.info("Inner generateReport Dauer = "+ diff);
		if (generatedWithoutError) {
			if (customer.getInvoiceConfiguration().getCreationFrequency()
					.equals(Model.FREQUENCY_YEARLY)) {
				/*
				 * Bei Jahreskunden muss das LastCalculationDate auf den Karten
				 * gespeichert werden. Jahresrechnungen werden jeden Monat
				 * erstellt mit den Karten, die seit der letzten
				 * Rechnungserstellung (normalerweise im letzten Monat)
				 * hinzugekommen sind. Es werden nur Rechnungen f�r ganze Monate
				 * erstellt - das hei�t, wenn am 5. eines Monats ein
				 * Rechnungslauf stattfindet, dann werden die Karten, die zw. 1.
				 * und 5. dieses Monats aktiviert wurden, noch nicht beachtet -
				 * erst im Folgemonat. Eigentlich ist nur das Jahr relevant,
				 * aber historisch bedingt wird das ganze Datum gespeichert.
				 * Wenn eine Karte in einem Jahr schon einmal in Rechnung
				 * gestellt wurde (f�r das ganze Jahr ab dem Monat der
				 * Aktivierung), dann muss die Karte erst im Folgejahr wieder in
				 * Rechnung gestellt werden.
				 */
				Iterator<DaoObject> cIt = customerCards.iterator();
				while (cIt.hasNext()) {
					CardBean card = (CardBean) cIt.next();
					card.setLastCalculationYear(calcMonth.get(Calendar.YEAR));
				}
			} else {
				/*
				 * Bei allen anderen Rechnungsl�ufen muss nur pro Kunde das
				 * LastCalculationDate gesetzt werden.
				 */
				
				long time3 = System.currentTimeMillis();
				CustomerController cc = new CustomerController();
				Customer dbCus = cc.findCustomer(customer.getCustomernumber());
				dbCus.setLastCalculationDate(calcMonth.getTime());
				long time4 = System.currentTimeMillis();
				long diff2 = time4 - time3;
				System.out.println("Inner generateReport Dauer = "+ diff2);

			}
		} else {
			// Aus der Schleife f�r diesen Kunden aussteigen
			// Folgemonate d�rfen nicht mehr berechnet werden
			// Mit n�chstem Kunde weitermachen
			logger.error("Fehler bei Report-Erstellung f�r Kunde: "
					+ customer.getCustomernumber() + ", Monat: "
					+ DateUtils.getMonthAsString(calcMonth.get(Calendar.MONTH))
					+ " " + calcMonth.get(Calendar.YEAR));
			return false;
		}

		return true;
	}

	public int getProgress() {
		if (new FacesUtil().getSession().getAttribute("reportProgress") != null) {
			System.out.println(""
					+ new FacesUtil().getSession().getAttribute(
							"reportProgress") + " %");
			return (Integer) new FacesUtil().getSession().getAttribute(
					"reportProgress");
		} else {
			System.out.println("-1 %");
			return -1;
		}
	}

	private Calendar raiseMonth(Calendar calcMonth, Customer customer) {
		String creationFrequency = null;
		if (customer.getInvoiceConfiguration() != null) {
			creationFrequency = customer.getInvoiceConfiguration()
					.getCreationFrequency();
		}

		if (creationFrequency != null) {
			if (creationFrequency.equals(Model.FREQUENCY_MONTHLY)) {
				calcMonth.add(Calendar.MONTH, 1);
			} else if (creationFrequency.equals(Model.FREQUENCY_QUARTERLY)) {
				calcMonth.add(Calendar.MONTH, 3);
			} else if (creationFrequency.equals(Model.FREQUENCY_HALFYEARLY)) {
				calcMonth.add(Calendar.MONTH, 6);
			} else if (creationFrequency.equals(Model.FREQUENCY_YEARLY)) {
				// Jahreskunden bekommen monatlich eine inkrementelle Rechnung
				calcMonth.add(Calendar.MONTH, 1);
			}
		} else {
			// treat like monthly creation
			logger.warn("No CreationFrequency set for customer "
					+ customer.getCustomernumber() + "; Creating monthly!");
			calcMonth.add(Calendar.MONTH, 1);
		}

		return calcMonth;
	}

	private List<DaoObject> searchCustomers() {
		String select = "select distinct customer from Customer customer where customer.customernumber != ''";
		// String select =
		// "select distinct customer from Customer customer where customer.customernumber IN ("
		// +
		// "'20166', '20120', '20016', '20190', '20074', '20208', '20206', '20216', '20198'"
		// +
		// ", '20200', '20039', '20128', '20157', '20076', '20224'" +
		// ", '20012', '20105', '20197', '20060', '20218', '20066', '20209', '20107', '20094'"
		// +
		// ", '20069', '20112', '20201', '20079', '20098', '20165', '20214'" +
		// ")";

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = createTransaction(session);
		List<DaoObject> customerList = searchObjects(select, tx, session);
		tx.commit();
		return customerList;
	}

	private List<DaoObject> searchCards(Customer customer, Calendar calcMonth,
			Transaction tx, Session session, boolean flatrateCalc) {
		Calendar maxActivationDate = Calendar.getInstance();
		maxActivationDate.set(new Integer(calcMonth.get(Calendar.YEAR)),
				new Integer(calcMonth.get(Calendar.MONTH)), 1, 0, 0, 0);
		maxActivationDate = addMonthsToMaxActivationDate(customer
				.getInvoiceConfiguration().getCreationFrequency(),
				maxActivationDate);
		Calendar maxLastCalculationDate = Calendar.getInstance();
		maxLastCalculationDate.set(new Integer(calcMonth.get(Calendar.YEAR)),
				new Integer(calcMonth.get(Calendar.MONTH)), 1, 0, 0, 0);
		maxLastCalculationDate.set(Calendar.MILLISECOND, 0);
		Calendar customerLastCalcDate = Calendar.getInstance();
		if (customer.getLastCalculationDate() != null) {
			customerLastCalcDate.setTime(customer.getLastCalculationDate());
		}

		if (customer.getLastCalculationDate() == null
				|| customerLastCalcDate.before(maxLastCalculationDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String select = "select distinct card from CardBean card where card.customer = '"
					+ customer.getId()
					+ "' and card.status = 'aktiv' and card.activationDate < '"
					+ sdf.format(maxActivationDate.getTime())
					+ "' and NOT card.flatrateCard IS TRUE";
			if (customer.getInvoiceConfiguration().getCreationFrequency()
					.equals(Model.FREQUENCY_YEARLY)) {
				select = "select distinct card from CardBean card where card.customer = '"
						+ customer.getId()
						+ "' and card.status = 'Aktiv' and card.activationDate < '"
						+ sdf.format(maxActivationDate.getTime())
						+ "' and (card.lastCalculationYear IS NULL or card.lastCalculationYear < '"
						+ maxLastCalculationDate.get(Calendar.YEAR)
						+ "') and NOT card.flatrateCard IS TRUE";
			}
			if (flatrateCalc) {
				select = "select distinct card from CardBean card where card.customer = '"
						+ customer.getId()
						+ "' and card.status = 'Aktiv' and card.activationDate < '"
						+ sdf.format(maxActivationDate.getTime())
						+ "' and card.flatrateCard IS TRUE";
				if (customer.getInvoiceConfiguration().getCreationFrequency()
						.equals(Model.FREQUENCY_YEARLY)) {
					select = "select distinct card from CardBean card where card.customer = '"
							+ customer.getId()
							+ "' and card.status = 'Aktiv' and card.activationDate < '"
							+ sdf.format(maxActivationDate.getTime())
							+ "' and (card.lastCalculationYear IS NULL or card.lastCalculationYear < '"
							+ maxLastCalculationDate.get(Calendar.YEAR)
							+ "') and card.flatrateCard IS TRUE";
				}
			}

			DateComparator dc = new DateComparator();
			List<DaoObject> cardList = searchObjects(select, tx, session);
			if (cardList != null && cardList.size() > 0) {
				System.out.println("Size = " + cardList.size());
			}
			Collections.sort(cardList, dc);
			return cardList;
		}
		return null;
	}

	private Calendar addMonthsToMaxActivationDate(String creationFrequency,
			Calendar maxActivationDate) {
		if (creationFrequency.equals(Model.FREQUENCY_MONTHLY))
			maxActivationDate.add(Calendar.MONTH, 1);
		else if (creationFrequency.equals(Model.FREQUENCY_QUARTERLY))
			maxActivationDate.add(Calendar.MONTH,
					(maxActivationDate.get(Calendar.MONTH) + 1) % 3);
		else if (creationFrequency.equals(Model.FREQUENCY_HALFYEARLY))
			maxActivationDate.add(Calendar.MONTH,
					(maxActivationDate.get(Calendar.MONTH) + 1) % 6);
		else {
			// JAHRESKUNDE, zu behandeln wie Monatskunde
			maxActivationDate.add(Calendar.MONTH, 1);
			// maxActivationDate.add(Calendar.YEAR, 1);
			// maxActivationDate.set(Calendar.MONTH, 0);
		}
		return maxActivationDate;
	}

	private Transaction createTransaction(Session session) {
		Transaction tx = null;
		tx = session.beginTransaction();
		return tx;
	}

	@SuppressWarnings("unchecked")
	private List<DaoObject> searchObjects(String select, Transaction tx,
			Session session) {
		List<DaoObject> objects = null;
		objects = (List<DaoObject>) session.createQuery(select).list();
		return objects;
	}

	@Override
	public void run() {
		this.calculate();
	}

	public void startTask(ActionEvent event) {
		thread = new Thread(this);
		thread.start();
		ProgressBarTaskManager threadBean = (ProgressBarTaskManager) FacesUtil
				.getManagedBean(ProgressBarTaskManager.BEAN_NAME);
		threadBean.startThread(10, 10, 100);
	}

	public void downloadMonthBills() {
		System.out.println("Jetzt");
		BillController bc = new BillController();
		List<Bill> bills = bc.findMonthBills(2014, 1);
		
		if (bills != null) {

			System.out.println("Gefundene Rechnungen: " + bills.size());

			String testFilename = null;
			if (bills.size() > 3) {
				testFilename = bills.get(3).getFilename(); 
			}
			
			for (Bill bill: bills) {
				File file = new File(bill.getFilename());
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(file);
//					ZipOutputStream zos = new ZipOutputStream(fos);
//					ZipEntry ze = new ZipEntry(bill.getFilename());
//					ze.
//					zos.putNextEntry(ze);
					fos.write(bill.getFile());
					fos.flush();
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("WEB-INF/../Invoices/");
//			String path = getServletContext().getRealPath("WEB-INF/../");
			File file = new File(path);
			String fullPathToYourWebappRoot = null;
			try {
				fullPathToYourWebappRoot = file.getCanonicalPath();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			System.out.println("Pfad: " + fullPathToYourWebappRoot);
			File zipFile = new File(fullPathToYourWebappRoot,  "Siwaltec_Rechnungen.zip");
			FileOutputStream zfos;
			try {
				zfos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(zfos);
				byte[] buffer = new byte[1024];
				for (Bill bill: bills) {
					ZipEntry ze = new ZipEntry(bill.getFilename());
					zos.putNextEntry(ze);

					File f = new File(bill.getFilename());
					FileInputStream fis = new FileInputStream(f);
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					
//						zos.write(bill.getFile());
					zos.closeEntry();
					fis.close();
				}
				zos.close();
				
//					ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//					HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//					ServletContext servletContext = (ServletContext) externalContext.getContext();
//					response.reset();
//					response.setContentType(servletContext.getMimeType(zipFile.getName()));
//					response.setContentLength((int) zipFile.length());
//					response.setHeader("Content-Disposition", "attachment"+ 
//		                                     "filename=" + zipFile.getName());
//		            FileInputStream stream = new FileInputStream(zipFile);
//		            response.setContentLength(stream.available());
//		            OutputStream os = response.getOutputStream();      
//		            os.close();
//		            response.flushBuffer();			

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			try {
//				Runtime.getRuntime().exec(
//				"rundll32 url.dll,FileProtocolHandler " + testFilename);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}

	}
	
	public Resource getZipResource() {
		return ZIP_RESOURCE;
	}
	
	public boolean getTaskRunning() {
		return taskRunning;
	}

	public void setTaskRunning(boolean taskRunning) {
		this.taskRunning = taskRunning;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public class DateComparator implements java.util.Comparator<DaoObject> {
		public int compare(DaoObject dao1, DaoObject dao2) {
			CardBean card1 = (CardBean) dao1;
			CardBean card2 = (CardBean) dao2;
			return (card1.getActivationDate() == card2.getActivationDate() ? 0
					: card1.getActivationDate().compareTo(
							card2.getActivationDate()));
		}
	}

}
