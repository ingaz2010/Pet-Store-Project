package pet.store.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Data
@NoArgsConstructor
public class PetStoreData {

	private Long petStoreId;
	private String petStoreName;
	private String petStoreAddress;
	private String petStoreCity;
	private String petStoreState;
	private String petStoreZip;
	private String petStorePhone;
	private Set<PetStoreCustomer> petStoreCustomers = new HashSet<>();
	private Set<PetStoreEmployee> petStoreEmployees = new HashSet<>();
	
	 // PetSoreData constructor
	public PetStoreData(PetStore petStore){
		petStoreId = petStore.getPetStoreId();
		petStoreName = petStore.getPetStoreName();
		petStoreAddress = petStore.getPetStoreAddress();
		petStoreCity = petStore.getPetStoreCity();
		petStoreState = petStore.getPetStoreState();
		petStoreZip = petStore.getPetStoreZip();
		petStorePhone = petStore.getPetStorePhone();
		
		//filling petStoreCustomers Set
		for(Customer customer : petStore.getCustomers()) {
			PetStoreCustomer petStoreCustomer = new PetStoreCustomer(customer);
			petStoreCustomers.add(petStoreCustomer);
		}
		
		for(Employee employee : petStore.getEmployees()) {
			PetStoreEmployee petStoreEmployee = new PetStoreEmployee(employee);
			petStoreEmployees.add(petStoreEmployee);
		}
	}
}
