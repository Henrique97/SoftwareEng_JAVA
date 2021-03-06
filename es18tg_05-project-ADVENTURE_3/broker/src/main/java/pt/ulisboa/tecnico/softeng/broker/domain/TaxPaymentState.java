package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.TaxInterface;
import pt.ulisboa.tecnico.softeng.tax.dataobjects.InvoiceData;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class TaxPaymentState extends TaxPaymentState_Base {
	public static final int MAX_REMOTE_ERRORS = 3;

	@Override
	public State getValue() {
		return State.TAX_PAYMENT;
	}

	@Override
	public void process() {
		try {
			InvoiceData invoiceData = new InvoiceData(getAdventure().getBroker().getNifAsSeller(),
					getAdventure().getClient().getNIF(), "ADVENTURE", getAdventure().getAmount(),
					getAdventure().getBegin());
			getAdventure().setInvoiceReference(TaxInterface.submitInvoice(invoiceData));
		} catch (TaxException be) {
			getAdventure().setState(State.UNDO);
			return;
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				getAdventure().setState(State.UNDO);
			}
			return;
		}

		getAdventure().setState(State.CONFIRMED);
	}

}
