package models;
 
import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;
 
@Entity
public class PaymentMethod extends Model {
 
	@Required
	public String paymentMethodToken;
				
	public Boolean retained;	
	public Boolean redacted;
	
	public String firstName;	
	public String lastName;
	
	public Date createdAt = new Date();
	
	public String getLabel() {
		return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "") + " (" + paymentMethodToken + ")";
	}	
}