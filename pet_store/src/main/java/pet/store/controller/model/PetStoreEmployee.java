package pet.store.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Employee;

@Data
@NoArgsConstructor
public class PetStoreEmployee {
	private Long employeeId;
	private String employeeFirstName;
	private String employeeLastName;
	private String employeePhone;
	private String employeeEmail;
	
	//constructor
	PetStoreEmployee(Employee employee){
		employeeFirstName = employee.getEmployeeFirstName();
		employeeLastName = employee.getEmployeeLastName();
		employeePhone = employee.getEmployeePhone();
		employeeEmail = employee.getEmployeeEmail();
	}
	
}