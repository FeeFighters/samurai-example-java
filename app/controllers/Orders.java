package controllers;

import utils.SamuraiGatewayFactory;

import com.feefighers.SamuraiGateway;

public class Orders extends CRUD {

	public static void credit(Long id) throws Exception {
		models.Order model = models.Order.findById(id);
		SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();
		com.feefighers.model.Transaction transaction = gateway.transaction().find(model.referenceId);
		com.feefighers.model.Transaction creditTransaction = gateway.transaction().credit(transaction, null, null);
		
		model.creditReferenceId = creditTransaction.getReferenceId();
		model.save();
		
		redirect("/orders/list");
	}
}