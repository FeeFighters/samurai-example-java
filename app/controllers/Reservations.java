package controllers;

import utils.SamuraiGatewayFactory;

import com.feefighters.SamuraiGateway;

public class Reservations extends CRUD {

	public static void capture(Long id) throws Exception {
		captureOrVoidOperation(true, id);
	}
	
	public static void voidOperation(Long id) throws Exception {
		captureOrVoidOperation(false, id);
	}
	
	protected static void captureOrVoidOperation(boolean capture, Long id) {
		models.Reservation model = models.Reservation.findById(id);
		SamuraiGateway gateway = SamuraiGatewayFactory.newInstance();
		com.feefighters.model.Transaction transaction = gateway.transaction().find(model.referenceId);
		
		if(capture) {
			com.feefighters.model.Transaction caputreTransaction = gateway.transaction().credit(transaction, null, null);			
			model.captureReferenceId = caputreTransaction.getReferenceId();
		} else {
			com.feefighters.model.Transaction voidTransaction = gateway.transaction().voidOperation(transaction, null);			
			model.voidReferenceId = voidTransaction.getReferenceId();
		}
		model.save();
		redirect("/reservations/list");
	}
}