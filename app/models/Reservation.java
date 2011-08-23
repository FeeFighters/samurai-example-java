package models;
 
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;
 
@Entity
@Table(name="PaymentReservation")
public class Reservation extends Model {
 
	@Required
	public String referenceId;
	
	public String article;
	public Double amount;
	
	public Date createdAt = new Date();
	
	public String captureReferenceId;
	public String voidReferenceId;

}