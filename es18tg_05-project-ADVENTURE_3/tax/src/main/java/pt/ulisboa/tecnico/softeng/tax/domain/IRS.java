package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.FenixFramework;
import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.softeng.tax.dataobjects.InvoiceData;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class IRS extends IRS_Base {


	public static IRS getIRS() {
		if (FenixFramework.getDomainRoot().getIrs() == null) {
			FenixFramework.getDomainRoot().setIrs((new IRS()));
		}
		return FenixFramework.getDomainRoot().getIrs();
	}

	private IRS() {
	}

	public TaxPayer getTaxPayerByNIF(String NIF) {
		for (TaxPayer taxPayer : getTaxPayerSet()) {
			if (taxPayer.getNIF().equals(NIF)) {
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
		// AQUI
		IRS irs = IRS.getIRS();
		Seller seller = (Seller) irs.getTaxPayerByNIF(invoiceData.getSellerNIF());
		Buyer buyer = (Buyer) irs.getTaxPayerByNIF(invoiceData.getBuyerNIF());
		ItemType itemType = irs.getItemTypeByName(invoiceData.getItemType());
		Invoice invoice = new Invoice(invoiceData.getValue(), invoiceData.getDate(), itemType, seller, buyer);

		return invoice.getReference();
	}

	public void removeItemTypes() {
		getItemTypeSet().clear();
	}

	public void removeTaxPayers() {
		getTaxPayerSet().clear();
	}

	public void clearAll() {
		removeItemTypes();
		removeTaxPayers();
	}

	public static void cancelInvoice(String reference) {
		if (reference == null || reference.isEmpty()) {
			throw new TaxException();
		}

		Invoice invoice = IRS.getIRS().getInvoiceByReference(reference);

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

	public void delete() {
		setRoot(null);

		for (TaxPayer payer : getTaxPayerSet()) {
			payer.delete();
		}

		for (ItemType item: getItemTypeSet()) {
			item.delete();
		}
		clearAll();
		deleteDomainObject();
	}
}
