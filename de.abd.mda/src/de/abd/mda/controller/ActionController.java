package de.abd.mda.controller;

import java.util.List;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import de.abd.mda.model.Model;
import de.abd.mda.persistence.dao.CardBean;
import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;
import de.abd.mda.report.ReportCalculator;
import de.abd.mda.report.ReportGenerator2;

public class ActionController implements ActionListener {

	// Model related variables
	private Model model;
	protected CardBean cardBean;

	// JavaServerFaces related variables
	protected UIOutput uiMessage;

	// Constructor
	public ActionController() {
		model = new Model();
		model.createModel();
		getSession().setAttribute("model", model);
	}
	
	// ***************************** Aktionen.xhtml actions (navigation)
	// **********************
	public String openCreateNewCardDialog() {
		cardBean = new CardBean();
		return "createCard";
	}

	public String openSearchDialog() {
		return "searchCard";
	}

	public String openUpdateCardDialog() {
		cardBean = new CardBean();
		return "updateCard";
	}

	public String openClearingDialog() {
		return "openClearingDialog";
	}

	public String openDeliveryBillDialog() {
		return "openDeliveryBillDialog";
	}

	public String generateReport() {
		ReportCalculator rc = new ReportCalculator();
		rc.calculate();
		
		return "Report";
	}

	public String openAddCustomerDialog() {
		cardBean = new CardBean();
		return "openAddCustomerDialog";
	}

	// ***************** Aktionen.xhtml actions end **************************

	// ********* weitere Actions ****************

	public String cancel() {
		uiMessage.setRendered(true);
		uiMessage
				.setValue("Aktion abgebrochen! Neue Karte wurde NICHT angelegt!");
		return "failure";
	}

	public void calendarPartialSubmit(ValueChangeEvent event) {
		getRequest().setAttribute("calendarPartialSubmit", true);
	}

	/***************** ActionListener **************/
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		// TODO Auto-generated method stub
		getRequest().setAttribute("clearCard", true);
		System.out.println("ID = " + event.getComponent().getId());
		uiMessage.setRendered(false);
	}

	/********* Weitere Methoden ****************/
	protected HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}

	protected HttpServletRequest getRequest() {
		return ((HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest());
	}

	public String getRequestMessage() {
		if (getRequest().getAttribute("message") != null)
			return "" + getRequest().getAttribute("message");
		return "";
	}

	// ********** Getter / Setter ************

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public UIOutput getUiMessage() {
		return uiMessage;
	}

	public void setUiMessage(UIOutput uiMessage) {
		this.uiMessage = uiMessage;
	}

	public CardBean getCardBean() {
		return cardBean;
	}

	public void setCardBean(CardBean cardBean) {
		this.cardBean = cardBean;
	}
}