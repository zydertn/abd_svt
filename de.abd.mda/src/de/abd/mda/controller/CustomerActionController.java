package de.abd.mda.controller;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


import de.abd.mda.persistence.dao.Address;
import de.abd.mda.persistence.dao.CardBean;
import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;
import de.abd.mda.persistence.dao.InvoiceConfiguration;
import de.abd.mda.persistence.dao.Person;
import de.abd.mda.persistence.dao.controller.CardController;
import de.abd.mda.persistence.dao.controller.CustomerController;
import de.abd.mda.persistence.hibernate.SessionFactoryUtil;
import de.abd.mda.util.FacesUtil;

public class CustomerActionController extends ActionController {

	private Customer customer;
	private boolean opened = false;
	private List<Customer> customerList;
	private String selectedCustomer;
	private boolean componentDisabled = true;
	private HtmlInputText branchBinding;
	private HtmlInputText streetBinding;
	private HtmlInputText housenumberBinding;
	private HtmlInputText postboxBinding;
	private HtmlInputText postcodeBinding;
	private HtmlInputText cityBinding;
	private HtmlSelectOneMenu genderBinding;
	private HtmlSelectOneMenu contactGenderBinding;
	private HtmlInputText contactFirstnameBinding;
	private HtmlInputText contactNameBinding;
	private HtmlInputText invoiceStreetBinding;
	private HtmlInputText invoiceHousenumberBinding;
	private HtmlInputText invoicePostboxBinding;
	private HtmlInputText invoicePostcodeBinding;
	private HtmlInputText invoiceCityBinding;
	private HtmlSelectOneMenu invoiceconfigSimpriceBinding;
	private HtmlSelectOneMenu invoiceconfigDataoptionBinding;
	private HtmlSelectOneMenu invoiceconfigFormatBinding;
	private HtmlSelectOneMenu invoiceconfigCreationFrequencyBinding;
	private HtmlSelectManyCheckbox invoiceconfigColumnsBinding;
	
	public CustomerActionController() {
		customer = new Customer();
		customerList = new ArrayList<Customer>();
	}
	
	public void createCustomer() {
		CustomerController customerController = new CustomerController();
		
		String retMessage = customerController.createObject(customer);

		if (retMessage != null && retMessage.length() > 0) {
			getRequest().setAttribute("message", retMessage);
		} else {
			getRequest().setAttribute("message", "Neuer Kunde wurde erfolgreich angelegt!" + customer.getCustomernumber());
		}
		getSession().setAttribute("mycustomer", customer);
		getSession().setAttribute("refreshCustomerList", true);
	}

	public void searchCustomer() {
		CustomerController cc = new CustomerController();
		List<DaoObject> customers =	cc.searchCustomer(customer.getCustomernumber(), customer.getName());
		customerList = new ArrayList<Customer>();
		
		if (customers != null && customers.size() > 0) {
			System.out.println(customers.size() + " Kunden gefunden");
			
			if (customers.size() > 1) {
				Iterator it = customers.iterator();
				while (it.hasNext()) {
					Customer c = (Customer) it.next();
					customerList.add(c);
				}
				opened = !opened;
			} else {
				customer = (Customer) customers.get(0);
			}
		} else {
			System.out.println("Kein Customer gefunden");
		}
	}
	
	public String updateCustomer(Customer customer) {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		List<DaoObject> customers = null;
		try {
			tx = session.beginTransaction();
			String whereClause = "";
			whereClause = " where customer.id = '" + customer.getId() + "'";
			
			List<Customer> customerList = session.createQuery("from Customer as customer" + whereClause).list();
			Customer dbCustomer = customerList.get(0);
			dbCustomer.setCustomernumber(customer.getCustomernumber());
			dbCustomer.setBranch(customer.getBranch());
			dbCustomer.setName(customer.getName());
			
			Address ad = dbCustomer.getAddress();
			Address cad = customer.getAddress();
			ad.setCity(cad.getCity());
			ad.setHousenumber(cad.getHousenumber());
			ad.setPostbox(cad.getPostbox());
			ad.setPostcode(cad.getPostcode());
			ad.setStreet(cad.getStreet());
			
			Person cp = dbCustomer.getContactPerson();
			Person cpp = customer.getContactPerson();
			cp.setFirstname(cpp.getFirstname());
			cp.setGender(cpp.getGender());
			cp.setName(cpp.getName());
			Address cpa = cp.getAddress();
			Address cppa = cpp.getAddress();
			cpa.setCity(cppa.getCity());
			cpa.setHousenumber(cppa.getHousenumber());
			cpa.setPostbox(cppa.getPostbox());
			cpa.setPostcode(cppa.getPostcode());
			cpa.setStreet(cppa.getStreet());

			Address ia = dbCustomer.getInvoiceAddress();
			Address cia = customer.getInvoiceAddress();
			ia.setCity(cia.getCity());
			ia.setHousenumber(cia.getHousenumber());
			ia.setPostbox(cia.getPostbox());
			ia.setPostcode(cia.getPostcode());
			ia.setStreet(cia.getStreet());

			InvoiceConfiguration ic = dbCustomer.getInvoiceConfiguration();
			InvoiceConfiguration cic = customer.getInvoiceConfiguration();
			ic.setColumns(cic.getColumns());
			ic.setCreationFrequency(cic.getCreationFrequency());
			ic.setDataOptionSurcharge(cic.getDataOptionSurcharge());
			ic.setFormat(cic.getFormat());
			ic.setSimPrice(cic.getSimPrice());
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					System.out.println("Error rolling back transaction");
				}
				// throw again the first exception
				throw e;
			}
		}

		FacesUtil.writeAttributeToRequest("message", "�nderung gespeichert!");
		customer = new Customer();
		if (FacesContext.getCurrentInstance() != null) {
			branchBinding.setDisabled(true);
			streetBinding.setDisabled(true);
			housenumberBinding.setDisabled(true);
			postboxBinding.setDisabled(true);
			postcodeBinding.setDisabled(true);
			cityBinding.setDisabled(true);
			contactGenderBinding.setDisabled(true);
			contactFirstnameBinding.setDisabled(true);
			contactNameBinding.setDisabled(true);
			invoiceStreetBinding.setDisabled(true);
			invoiceHousenumberBinding.setDisabled(true);
			invoicePostboxBinding.setDisabled(true);
			invoicePostcodeBinding.setDisabled(true);
			invoiceCityBinding.setDisabled(true);
			invoiceconfigSimpriceBinding.setDisabled(true);
			invoiceconfigDataoptionBinding.setDisabled(true);
			invoiceconfigFormatBinding.setDisabled(true);
			invoiceconfigCreationFrequencyBinding.setDisabled(true);
			invoiceconfigColumnsBinding.setDisabled(true);
		}

		
		return "openUpdateCustomerDialog";
	}
	
	public String createCustomerNext() {
		createCustomer();
		customer = new Customer();
		return null;
	}

	public String createCustomerFinish() {
		createCustomer();
		customer = new Customer();
		return "finish";
	}

	public void selectCustomer() {
		Iterator itrCust = customerList.iterator();
		while (itrCust.hasNext()) {
			Customer customerFromList = (Customer) itrCust.next();
			if (customerFromList.toString().equals(selectedCustomer)) {
				System.out.println("Customer " + customerFromList.getName()
						+ " equals my chosen customer!!!");
				customer = customerFromList;
				break;
			} else
				System.out.println("kein Match");
		}

		branchBinding.setDisabled(false);
		streetBinding.setDisabled(false);
		housenumberBinding.setDisabled(false);
		postboxBinding.setDisabled(false);
		postcodeBinding.setDisabled(false);
		cityBinding.setDisabled(false);
		contactGenderBinding.setDisabled(false);
		contactFirstnameBinding.setDisabled(false);
		contactNameBinding.setDisabled(false);
		invoiceStreetBinding.setDisabled(false);
		invoiceHousenumberBinding.setDisabled(false);
		invoicePostboxBinding.setDisabled(false);
		invoicePostcodeBinding.setDisabled(false);
		invoiceCityBinding.setDisabled(false);
		invoiceconfigSimpriceBinding.setDisabled(false);
		invoiceconfigDataoptionBinding.setDisabled(false);
		invoiceconfigFormatBinding.setDisabled(false);
		invoiceconfigCreationFrequencyBinding.setDisabled(false);
		invoiceconfigColumnsBinding.setDisabled(false);
		getRequest().setAttribute("componentDisabled", false);
		opened = !opened;
	}
	
	public String deleteCustomer() {
		CustomerController customerController = new CustomerController();
		
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String hql = "select cardBean from CardBean cardBean where customer = " + customer.getId();
		List<CardBean> results = session.createQuery(hql).list();

		if (results != null && results.size() > 0) {
			for (CardBean c: results) {
				c.setCustomer(new Customer());
			}
		}

		tx.commit();

		customerController.deleteObject(customer);
		getSession().setAttribute("refreshCustomerList", true);
		
		getRequest().setAttribute("message", "Kunde wurde erfolgreich gel�scht!");
		return "";
	}
	
	public Customer getCustomer() {
		if (getRequest().getAttribute("newCustomer") != null) {
			customer = new Customer();
			getRequest().removeAttribute("newCustomer");
		}
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	
	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

	public String getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(String selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public boolean isComponentDisabled() {
		if (getRequest().getAttribute("componentDisabled") != null)
			return true;
		return false;
	}

	public void setComponentDisabled(boolean componentDisabled) {
		this.componentDisabled = componentDisabled;
	}

	public HtmlInputText getHousenumberBinding() {
		if (invoicePostboxBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoicePostboxBinding.setDisabled(true);
		return housenumberBinding;
	}

	public void setHousenumberBinding(HtmlInputText housenumberBinding) {
		this.housenumberBinding = housenumberBinding;
	}

	public HtmlInputText getPostboxBinding() {
		if (invoicePostboxBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoicePostboxBinding.setDisabled(true);
		return postboxBinding;
	}

	public void setPostboxBinding(HtmlInputText postboxBinding) {
		this.postboxBinding = postboxBinding;
	}

	public HtmlInputText getPostcodeBinding() {
		if (postcodeBinding != null && getRequest().getAttribute("componentDisabled") != null)
			postcodeBinding.setDisabled(true);
		return postcodeBinding;
	}

	public void setPostcodeBinding(HtmlInputText postcodeBinding) {
		this.postcodeBinding = postcodeBinding;
	}

	public HtmlInputText getCityBinding() {
		if (cityBinding != null && getRequest().getAttribute("componentDisabled") != null)
			cityBinding.setDisabled(true);
		return cityBinding;
	}

	public void setCityBinding(HtmlInputText cityBinding) {
		this.cityBinding = cityBinding;
	}

	public HtmlSelectOneMenu getGenderBinding() {
		if (genderBinding != null && getRequest().getAttribute("componentDisabled") != null)
			genderBinding.setDisabled(true);
		return genderBinding;
	}

	public void setGenderBinding(HtmlSelectOneMenu genderBinding) {
		this.genderBinding = genderBinding;
	}

	public HtmlInputText getInvoiceStreetBinding() {
		if (invoiceStreetBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceStreetBinding.setDisabled(true);
		return invoiceStreetBinding;
	}

	public void setInvoiceStreetBinding(HtmlInputText invoiceStreetBinding) {
		this.invoiceStreetBinding = invoiceStreetBinding;
	}

	public HtmlInputText getInvoiceHousenumberBinding() {
		if (invoiceHousenumberBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceHousenumberBinding.setDisabled(true);
		return invoiceHousenumberBinding;
	}

	public void setInvoiceHousenumberBinding(HtmlInputText invoiceHousenumberBinding) {
		this.invoiceHousenumberBinding = invoiceHousenumberBinding;
	}

	public HtmlInputText getInvoicePostboxBinding() {
		if (invoicePostboxBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoicePostboxBinding.setDisabled(true);
		return invoicePostboxBinding;
	}

	public void setInvoicePostboxBinding(HtmlInputText invoicePostboxBinding) {
		this.invoicePostboxBinding = invoicePostboxBinding;
	}

	public HtmlInputText getInvoicePostcodeBinding() {
		if (invoicePostcodeBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoicePostcodeBinding.setDisabled(true);
		return invoicePostcodeBinding;
	}

	public void setInvoicePostcodeBinding(HtmlInputText invoicePostcodeBinding) {
		this.invoicePostcodeBinding = invoicePostcodeBinding;
	}

	public HtmlInputText getInvoiceCityBinding() {
		if (invoiceCityBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceCityBinding.setDisabled(true);
		return invoiceCityBinding;
	}

	public void setInvoiceCityBinding(HtmlInputText invoiceCityBinding) {
		this.invoiceCityBinding = invoiceCityBinding;
	}

	public HtmlSelectOneMenu getInvoiceconfigSimpriceBinding() {
		if (invoiceconfigSimpriceBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceconfigSimpriceBinding.setDisabled(true);
		return invoiceconfigSimpriceBinding;
	}

	public void setInvoiceconfigSimpriceBinding(
			HtmlSelectOneMenu invoiceconfigSimpriceBinding) {
		this.invoiceconfigSimpriceBinding = invoiceconfigSimpriceBinding;
	}

	public HtmlSelectOneMenu getInvoiceconfigDataoptionBinding() {
		if (invoiceconfigDataoptionBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceconfigDataoptionBinding.setDisabled(true);
		return invoiceconfigDataoptionBinding;
	}

	public void setInvoiceconfigDataoptionBinding(
			HtmlSelectOneMenu invoiceconfigDataoptionBinding) {
		this.invoiceconfigDataoptionBinding = invoiceconfigDataoptionBinding;
	}

	public HtmlSelectOneMenu getInvoiceconfigFormatBinding() {
		if (invoiceconfigFormatBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceconfigFormatBinding.setDisabled(true);
		return invoiceconfigFormatBinding;
	}

	public void setInvoiceconfigFormatBinding(
			HtmlSelectOneMenu invoiceconfigFormatBinding) {
		this.invoiceconfigFormatBinding = invoiceconfigFormatBinding;
	}

	public HtmlSelectOneMenu getInvoiceconfigCreationFrequencyBinding() {
		if (invoiceconfigCreationFrequencyBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceconfigCreationFrequencyBinding.setDisabled(true);
		return invoiceconfigCreationFrequencyBinding;
	}

	public void setInvoiceconfigCreationFrequencyBinding(
			HtmlSelectOneMenu invoiceconfigCreationFrequencyBinding) {
		this.invoiceconfigCreationFrequencyBinding = invoiceconfigCreationFrequencyBinding;
	}

	public HtmlSelectManyCheckbox getInvoiceconfigColumnsBinding() {
		if (invoiceconfigColumnsBinding != null && getRequest().getAttribute("componentDisabled") != null)
			invoiceconfigColumnsBinding.setDisabled(true);
		return invoiceconfigColumnsBinding;
	}

	public void setInvoiceconfigColumnsBinding(
			HtmlSelectManyCheckbox invoiceconfigColumnsBinding) {
		this.invoiceconfigColumnsBinding = invoiceconfigColumnsBinding;
	}

	public HtmlInputText getStreetBinding() {
		if (streetBinding != null && getRequest().getAttribute("componentDisabled") != null)
			streetBinding.setDisabled(true);
		return streetBinding;
	}

	public void setStreetBinding(HtmlInputText streetBinding) {
		this.streetBinding = streetBinding;
	}

	public HtmlInputText getContactFirstnameBinding() {
		if (contactFirstnameBinding != null && getRequest().getAttribute("componentDisabled") != null)
			contactFirstnameBinding.setDisabled(true);
		return contactFirstnameBinding;
	}

	public void setContactFirstnameBinding(HtmlInputText contactFirstnameBinding) {
		this.contactFirstnameBinding = contactFirstnameBinding;
	}

	public HtmlSelectOneMenu getContactGenderBinding() {
		if (contactGenderBinding != null && getRequest().getAttribute("componentDisabled") != null)
			contactGenderBinding.setDisabled(true);
		return contactGenderBinding;
	}

	public void setContactGenderBinding(HtmlSelectOneMenu contactGenderBinding) {
		this.contactGenderBinding = contactGenderBinding;
	}

	public HtmlInputText getContactNameBinding() {
		if (contactNameBinding != null && getRequest().getAttribute("componentDisabled") != null)
			contactNameBinding.setDisabled(true);
		return contactNameBinding;
	}

	public void setContactNameBinding(HtmlInputText contactNameBinding) {
		this.contactNameBinding = contactNameBinding;
	}

	public HtmlInputText getBranchBinding() {
		if (branchBinding != null && getRequest().getAttribute("componentDisabled") != null)
			branchBinding.setDisabled(true);
		return branchBinding;
	}

	public void setBranchBinding(HtmlInputText branchBinding) {
		this.branchBinding = branchBinding;
	}


}