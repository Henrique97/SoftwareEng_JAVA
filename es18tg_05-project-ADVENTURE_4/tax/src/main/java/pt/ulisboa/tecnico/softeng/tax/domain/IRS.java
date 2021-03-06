package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;
import pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects.InvoiceData;

public class IRS extends IRS_Base {

	public static IRS getIRSInstance() {
		if (FenixFramework.getDomainRoot().getIrs() == null) {
			return new IRS();
		}
		return FenixFramework.getDomainRoot().getIrs();
	}

	private IRS() {
		setRoot(FenixFramework.getDomainRoot());
	}

	public void delete() {
		setRoot(null);

		clearAll();

		deleteDomainObject();
	}

	public TaxPayer getTaxPayerByNIF(String NIF) {
		for (TaxPayer taxPayer : getTaxPayerSet()) {
			if (taxPayer.getNif().equals(NIF)) {
				return taxPayer;
			}
		}
		return null;
	}

	public ItemType getItemTypeByName(String name) {
		for (ItemType itemType : getItemTypeSet()) {
			if (itemType.getName().equals(name)) {
				return itemType;
			}
		}
		return null;
	}

	public static String submitInvoice(InvoiceData invoiceData) {
		IRS irs = IRS.getIRSInstance();
		Seller seller = (Seller) irs.getTaxPayerByNIF(invoiceData.getSellerNIF());
		Buyer buyer = (Buyer) irs.getTaxPayerByNIF(invoiceData.getBuyerNIF());
		ItemType itemType = irs.getItemTypeByName(invoiceData.getItemType());
		Invoice invoice = new Invoice(invoiceData.getValue(), invoiceData.getDate(), itemType, seller, buyer);

		return invoice.getReference();
	}

	private void clearAll() {
		for (ItemType itemType : getItemTypeSet()) {
			itemType.delete();
		}

		for (TaxPayer taxPayer : getTaxPayerSet()) {
			taxPayer.delete();
		}

		for (Invoice invoice : getInvoiceSet()) {
			invoice.delete();
		}

	}

	public static void cancelInvoice(String reference) {
		if (reference == null || reference.isEmpty()) {
			throw new TaxException();
		}

		Invoice invoice = IRS.getIRSInstance().getInvoiceByReference(reference);

		if (invoice == null) {
			throw new TaxException();
		}

		invoice.cancel();
	}

	private Invoice getInvoiceByReference(String reference) {
		for (TaxPayer taxPayer : getTaxPayerSet()) {
			Invoice invoice = taxPayer.getInvoiceByReference(reference);
			if (invoice != null) {
				return invoice;
			}
		}
		return null;
	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

}
