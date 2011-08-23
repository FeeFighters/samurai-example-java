package utils;

import play.Play;

import com.feefighers.SamuraiGateway;

public class SamuraiGatewayFactory {

    public static SamuraiGateway newInstance() {
    	String merchantKey = Play.configuration.getProperty("samurai.merchant_key");
    	String merchantPassword = Play.configuration.getProperty("samurai.merchant_password");
    	String processorToken = Play.configuration.getProperty("samurai.processor_token");
    	return new SamuraiGateway(merchantKey, merchantPassword, processorToken);
    }

}