package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		//find driver
		Driver driverbook=new Driver();
		Cab cabbooked=new Cab();
		List<Driver> driverList=driverRepository2.findAll();
		for(Driver driver: driverList){
			boolean available=driver.getCab().getAvailable();
			if(available) {
				driverbook=driver;
				cabbooked=driver.getCab();
				break;
			}
		}
		if(driverbook.getMobile()==null){
			throw new Exception("No cab available!");
		}

		//set trip attributes
		Customer customer=customerRepository2.findById(customerId).get();
		TripBooking tripBooking=new TripBooking();

		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setDriver(driverbook);
		tripBooking.setCustomer(customer);
		tripBooking.setTripStatus(TripStatus.CONFIRMED);
		int bill=cabbooked.getPerKmRate()*distanceInKm;
		tripBooking.setBill(bill);

		List<TripBooking> customertrips=customer.getTripBookingList();
		customertrips.add(tripBooking);
		customer.setTripBookingList(customertrips);

		//customerRepository2.save(customer);

		List<TripBooking> drivertrips=driverbook.getTripBookingList();
		drivertrips.add(tripBooking);
		driverbook.setTripBookingList(drivertrips);

		//driverRepository2.save(driverbook);

		tripBookingRepository2.save(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);

	}
}
