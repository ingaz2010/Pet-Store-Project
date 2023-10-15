package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	
	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;
	
	
	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);
		
		copyPetStoreFields(petStore, petStoreData);
		
		return new PetStoreData(petStoreDao.save(petStore));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		if(Objects.isNull(petStoreId)) {
			
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
		//petStore = findPetStoreById(petStoreId);
		return petStore;
	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet Store with ID=" + petStoreId + " is not found."));
	}
	
	public List<PetStoreData> retrieveAllPetStores(){
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> response = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			psd.getPetStoreCustomers().clear();
			psd.getPetStoreCustomers().clear();
			
			response.add(psd);
		}
		return response;
	}

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		Long employeeId = petStoreEmployee.getEmployeeId();
		PetStore petStore = findPetStoreById(petStoreId);
		
		Employee employee = findOrCreateEmployee(employeeId, petStoreId);
		copyEmpoyeeFields(employee, petStoreEmployee);
		
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		Employee dbEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(dbEmployee);
	}
	
	public Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId).orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " is not found"));
		if(employee.getPetStore().getPetStoreId() == petStoreId) {
			return employee;
		} else {
			throw new IllegalArgumentException("PetStore with ID=" + employee.getPetStore().getPetStoreId() + " does not exists and employee cannot be assosiated with it");
		}	
	}
	
	public Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {
		Employee employee;
		if(Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(petStoreId, employeeId);
		}
		return employee;
	}
	
	public void copyEmpoyeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeEmail(petStoreEmployee.getEmployeeEmail());
	}

	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		return new PetStoreData(petStore);
	}

	
	@Transactional(readOnly = false)
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
	}

	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		Long customerId = petStoreCustomer.getCustomerId();
		PetStore petStore = findPetStoreById(petStoreId);
		Customer customer = findOrCreateCustomer(customerId, petStoreId);
		
		copyCustomerFields(customer, petStoreCustomer);
		
		customer.setPetStores(null);
		petStore.getCustomers().add(customer);
		
		Customer dbCustomer = customerDao.save(customer);
		
		return new PetStoreCustomer(dbCustomer);
	}

	public Customer findCustomerById(Long petStoreId, Long customerId) {	
		Customer customer = customerDao.findById(customerId).orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " Is not found"));
		PetStoreCustomer found = null ;
		List<PetStoreData> petStores = retrieveAllPetStores();
		
		for(PetStoreData petStore : petStores) {
			Set<PetStoreCustomer> customers = petStore.getPetStoreCustomers();
			for(PetStoreCustomer storeCustomer : customers) {
				if(storeCustomer.getCustomerId()==customerId) {
					found = storeCustomer;
				}
			}
		}
		
		/*for(PetStore petStore : customer.getPetStores()) {
			if(petStore.getPetStoreId()==petStoreId) {
				found = customer;
			}else {
				found = null;
			}
		}
		if(Objects.isNull(found)){
			throw new IllegalArgumentException("Pet Store ID=" + petStoreId + " does not match records with customer with ID=" + customerId);
		} else {
		return found;
		}*/
	
		copyCustomerFields(customer, found);
		Set<PetStore> customerPetStores = customer.getPetStores();
		customerPetStores.add(findPetStoreById(petStoreId));
		return customer;
	}
	
	public Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
		Customer customer;
		if(Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(petStoreId, customerId);
		}
		return customer;
	}
	
	public void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
	}
	
	
}
