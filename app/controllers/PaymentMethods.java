package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import utils.SamuraiGatewayFactory;

import com.feefighers.SamuraiGateway;

public class PaymentMethods extends CRUD {
	public static void blank() throws Exception {
		if(params.get("payment_method_token") != null) {
			SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();			
			com.feefighers.model.PaymentMethod pm = gateway.processor().find(params.get("payment_method_token"));				
			if(!createPaymentMethodOrAddValidationErrors(pm)) {
				render("/PaymentMethods/show.html", pm);
			} else {
				redirect("/paymentmethods/list");
			}
		} else {
			com.feefighers.model.PaymentMethod pm = new com.feefighers.model.PaymentMethod();
			render(pm);
		}
	}	
	
	public static void show(Long id) throws Exception {
		models.PaymentMethod model = models.PaymentMethod.findById(id);
		com.feefighers.model.PaymentMethod pm = SamuraiGatewayFactory.newInstance().processor().find(model.paymentMethodToken);
		render(pm);		
	}
	
	public static void view(Long id) throws Exception {
		models.PaymentMethod model = models.PaymentMethod.findById(id);
		com.feefighers.model.PaymentMethod pm = SamuraiGatewayFactory.newInstance().processor().find(model.paymentMethodToken);
		render(pm);
	}	
	
	public static void create() throws Exception {
		if(params.get("payment_method_token") != null) {
			SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();			
			com.feefighers.model.PaymentMethod pm = gateway.processor().find(params.get("payment_method_token"));				
			if(!createPaymentMethodOrAddValidationErrors(pm)) {
				render("/PaymentMethods/show.html", pm);
			}			
		}
		redirect("/paymentmethods/list");
	}	
			
	public static void redact(Long id) throws Exception {
		redactOrRetain(id, false);
	}
	
	public static void retain(Long id) throws Exception {
		redactOrRetain(id, true);
	}	
	
	protected static void redactOrRetain(Long id, boolean retain) {
		models.PaymentMethod model = models.PaymentMethod.findById(id);
		SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();
		com.feefighers.model.PaymentMethod pm = gateway.processor().find(model.paymentMethodToken);
		
		if(retain) {
			gateway.processor().retain(pm);
			model.retained = true;
		} else {
			gateway.processor().redact(pm);
			model.redacted = true;	
		}
		model.save();
		redirect("/paymentmethods/list");
	}
	
	public static void buy(Long id) throws Exception {
		buyOrReserve(id, true);
	}
	
	public static void reserve(Long id) throws Exception {
		buyOrReserve(id, false);
	}	
	
	protected static void buyOrReserve(Long id, boolean buy) {
		boolean expandPm = false;
		com.feefighers.model.PaymentMethod pm = null;
		if(params.get("payment_method_token") != null) {
			SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();			
			pm = gateway.processor().find(params.get("payment_method_token"));			
			
			if(createPaymentMethodOrAddValidationErrors(pm)) {
				redirect("/paymentmethods/" + (buy ? "buy" : "reserve") + "?id=" + id);				
			} else {
				expandPm = true;
			}			
		} else {
			pm = new com.feefighers.model.PaymentMethod();			
		}
		
		models.Article article = models.Article.findById(id);		
		List paymentMethods = models.PaymentMethod.find("order by id desc").fetch(); 
		render(article, paymentMethods, pm, expandPm);
	}
	
	public static void purchase() throws Exception {
		purchaseOrAuthorise(true);
	}
	
	public static void authorise() throws Exception {
		purchaseOrAuthorise(false);
	}	
	
	protected static void purchaseOrAuthorise(boolean purchase) {
		models.Article article = models.Article.findById(Long.valueOf(params.get("articleId")));
		models.PaymentMethod pm = models.PaymentMethod.findById(Long.valueOf(params.get("paymentMethodId")));
		
		SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();
		
		if(purchase) {
			com.feefighers.model.Transaction transaction = gateway.processor().purchase(pm.paymentMethodToken, article.amount, null);
			
			models.Order localTransaction = new models.Order();
			localTransaction.referenceId = transaction.getReferenceId();
			localTransaction.article = article.name;
			localTransaction.amount = article.amount;
			localTransaction.save();
		} else {
			com.feefighers.model.Transaction transaction = gateway.processor().authorize(pm.paymentMethodToken, article.amount, null);

			models.Reservation localTransaction = new models.Reservation();
			localTransaction.referenceId = transaction.getReferenceId();
			localTransaction.article = article.name;
			localTransaction.amount = article.amount;
			localTransaction.save();
		}
		
		redirect("/" + (purchase ? "orders" : "reservations") + "/list");
	}
	
	protected static boolean createPaymentMethodOrAddValidationErrors(com.feefighers.model.PaymentMethod pm) {				
		if(pm.getSensitiveDataValid()) {
			models.PaymentMethod model = new models.PaymentMethod();
			model.paymentMethodToken = pm.getPaymentMethodToken();
			model.redacted = pm.getRedacted();
			model.retained = pm.getRetained();
			model.firstName = pm.getFirstName();
			model.lastName = pm.getLastName();
			model.save();
			return true;
		} else {
			for(com.feefighers.model.Message message : pm.getMessageList().getList()) {
				validation.addError("", getHumanName(message.getContext() + " " + message.getKey()));
			}	
			return false;
		}			
	}	
	
	protected static String getHumanName(String name) {
		name = StringUtils.replace(name, ".", " ");
		name = StringUtils.replace(name, "_", " ");
		name = StringUtils.capitalize(name);
		return name;
	}	
}