package pt.ulisboa.tecnico.softeng.car.domain;

import static org.junit.Assert.*;

import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RentACarGetAllAvailableVehiclesTest {

	private static final String NAME1 = "eartz";
	private static final String NAME2 = "eartz";
	private static final String NIF1 = "NIF1";
	private static final String NIF2 = "NIF2";
	private static final String IBAN1 = "IBAN1";
	private static final String IBAN2 = "IBAN2";
	private static final String PLATE_CAR1 = "aa-00-11";
	private static final String PLATE_CAR2 = "aa-00-22";
	private static final String PLATE_MOTORCYCLE = "44-33-HZ";
	private static final String DRIVING_LICENSE = "br123";
	private static final LocalDate date1 = LocalDate.parse("2018-01-06");
	private static final LocalDate date2 = LocalDate.parse("2018-01-07");
	private static final LocalDate date3 = LocalDate.parse("2018-01-08");
	private static final LocalDate date4 = LocalDate.parse("2018-01-09");
	private static final String RENTING_NIF = "987654321";
	private static final String RENTING_IBAN = "IBAN2";
	
	private RentACar rentACar1;
	private RentACar rentACar2;

	@Before
	public void setUp() {
		this.rentACar1 = new RentACar(NAME1, NIF1, IBAN1);
		this.rentACar2 = new RentACar(NAME2, NIF2, IBAN2);
	}

	@Test
	public void onlyCars() {
		Vehicle car1 = new Car(PLATE_CAR1, 10, rentACar1,50);
		car1.rent(DRIVING_LICENSE, date1, date2, RENTING_NIF, RENTING_IBAN);
		Vehicle car2 = new Car(PLATE_CAR2, 10, rentACar2,50);
		Vehicle motorcycle = new Motorcycle(PLATE_MOTORCYCLE, 10, rentACar1,50);

		Set<Vehicle> cars = RentACar.getAllAvailableCars(date3, date4);
		assertTrue(cars.contains(car1));
		assertTrue(cars.contains(car2));
		assertFalse(cars.contains(motorcycle));
	}

	@Test
	public void onlyAvailableCars() {
		Vehicle car1 = new Car(PLATE_CAR1, 10, rentACar1,50);
		Vehicle car2 = new Car(PLATE_CAR2, 10, rentACar2,50);

		car1.rent(DRIVING_LICENSE, date1, date2, RENTING_NIF, RENTING_IBAN);
		Set<Vehicle> cars = RentACar.getAllAvailableCars(date1, date2);

		assertFalse(cars.contains(car1));
		assertTrue(cars.contains(car2));
	}
	
	@Test
	public void onlyMotorcycles() {
		Vehicle car = new Car(PLATE_CAR1, 10, rentACar1,50);
		Vehicle motorcycle = new Motorcycle(PLATE_MOTORCYCLE, 10, rentACar1,50);

		Set<Vehicle> cars = RentACar.getAllAvailableMotorcycles(date3, date4);
		assertTrue(cars.contains(motorcycle));
		assertFalse(cars.contains(car));
	}

	@After
	public void tearDown() {
		RentACar.rentACars.clear();
		Vehicle.plates.clear();
	}
}
