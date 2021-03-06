package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Booking extends Booking_Base {
	private static final String HOUSING_TYPE = "HOUSING";

	public Booking(Room room, LocalDate arrival, LocalDate departure, String buyerNIF, String buyerIban) {
		checkArguments(room, arrival, departure, buyerNIF, buyerIban);

		setCancelledInvoice(false);
		setCancelledPaymentReference(null);

		setReference(room.getHotel().getCode() + Integer.toString(room.getHotel().getCounter()));
		setArrival(arrival);
		setDeparture(departure);

		setPrice(room.getHotel().getPrice(room.getType()) * Days.daysBetween(arrival, departure).getDays());
		setNif(buyerNIF);
		setBuyerIban(buyerIban);
		setProviderNif(room.getHotel().getNif());

		setRoom(room);
	}

	public void delete() {
		setRoom(null);
        setProcessor(null);
		deleteDomainObject();
	}

	private void checkArguments(Room room, LocalDate arrival, LocalDate departure, String buyerNIF, String buyerIban) {
		if (room == null || arrival == null || departure == null || buyerNIF == null || buyerNIF.trim().length() == 0
				|| buyerIban == null || buyerIban.trim().length() == 0) {
			throw new HotelException();
		}

		if (departure.isBefore(arrival)) {
			throw new HotelException();
		}
	}

	public static String getType() {
		return HOUSING_TYPE;
	}

	boolean conflict(LocalDate arrival, LocalDate departure) {
		if (isCancelled()) {
			return false;
		}

		if (arrival.equals(departure)) {
			return true;
		}

		if (departure.isBefore(arrival)) {
			throw new HotelException();
		}

		if ((arrival.equals(getArrival()) || arrival.isAfter(getArrival())) && arrival.isBefore(getDeparture())) {
			return true;
		}

		if ((departure.equals(getDeparture()) || departure.isBefore(getDeparture()))
				&& departure.isAfter(getArrival())) {
			return true;
		}

		if (arrival.isBefore(getArrival()) && departure.isAfter(getDeparture())) {
			return true;
		}

		return false;
	}

	public boolean isCancelledInvoice() {
		return getCancelledInvoice();
	}

	public String cancel() {
		setCancellation(getReference() + "CANCEL");
		setCancellationDate(new LocalDate());

		getRoom().getHotel().getProcessor().submitBooking(this);

		return getCancellation();
	}

	public boolean isCancelled() {
		return getCancellation() != null;
	}

	public String getIban() {
		return getBuyerIban();
	}
}
