package controllers;

import utils.SamuraiGatewayFactory;

import com.feefighters.SamuraiGateway;

public class Orders extends CRUD {

	public static void credit(Long id) throws Exception {
		models.Order model = models.Order.findById(id);
		SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();
		com.feefighters.model.Transaction transaction = gateway.transaction().find(model.referenceId);
		com.feefighters.model.Transaction creditTransaction = gateway.transaction().credit(transaction, null, null);
		
		model.creditReferenceId = creditTransaction.getReferenceId();
		model.save();
		
		redirect("/orders/list");
	}
}