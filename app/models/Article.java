package models;
 
import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;
 
@Entity
public class Article extends Model {
 
	@Required
    public String name;

	@Required
    public Double amount;
    
    public Article(String name, Double amount) {
        this.name = name;
        this.amount = amount;
    }
 
}