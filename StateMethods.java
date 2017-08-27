package com.paytm.fulfilment.stateTransition;

import java.util.Map;

import com.paytm.fulfillment.responseList.ResponseObject;
import com.paytm.fulfilment.model.type.AcknowledgeOrderRequest;
import com.paytm.fulfilment.model.type.FulfillmentUpdateRequest;
import com.paytm.fulfilment.model.type.ItemsV2Request;
import com.paytm.fulfilment.model.type.ItemsV2Response;
import com.paytm.fulfilment.model.type.ShipmentCreationRequest;
import com.paytm.fulfilment.model.type.ShipmentCreationResponse;
import com.paytm.fulfilment.model.type.UpdateImeiRequest;
import com.paytm.fulfilment.model.type.UpdateImeiResponse;
import com.paytm.fulfilment.model.type.V1CancelOrderRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class StateMethods {
	
	private ResponseObject responseObject = new ResponseObject();
	private String fulfillmentId;

	public void authorize(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) {

		System.out.println("I am in Authorized");
	}

	public void acknowledgeOrder(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		AcknowledgeOrderRequest ackRequest = new AcknowledgeOrderRequest();
		responseObject.setAcknowledgeOrderResponse(
				ackRequest.setItem_ids(null).setMerchantId(merchantId).setOrderId(orderId).setStatus(1).call());
		log.info("moved to status 5 ");
	}

	public void shipmentCreation(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		ShipmentCreationRequest shpCreationRequest = new ShipmentCreationRequest();
		ShipmentCreationResponse response = shpCreationRequest.setMerchantId(merchantId).setOrderId(orderId)
				.setPost_actions("false").setShipper_id(orderDetailMap.get("shipperId")).setTracking_number("12345678")
				.call();
		responseObject.setShipmentCreationResponse(response);
		fulfillmentId = response.getFulfillment_id();
		log.info("moved to status 23 ");

	}

	public void markReadyToShip(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		imeiUpdate(authToken, orderId, merchantId);
		FulfillmentUpdateRequest ffUpdateRequest = new FulfillmentUpdateRequest();
		responseObject.setMarkReadyToShipResponse(
				ffUpdateRequest.setFulfillment_id(fulfillmentId).setMerchantId(merchantId).setStatus("13").call());
		log.info("moved to status 13");
	}

	public void createManifest(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		FulfillmentUpdateRequest ffUpdateRequest = new FulfillmentUpdateRequest();
		responseObject.setCreateManifestResponse(
				ffUpdateRequest.setFulfillment_id(fulfillmentId).setMerchantId(merchantId).setStatus("25").call());

		log.info("moved to status 25");
	}

	public void moveToShipped(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		FulfillmentUpdateRequest ffUpdateRequest = new FulfillmentUpdateRequest();
		responseObject.setTrackShipmentUpdateResponse(
				ffUpdateRequest.setFulfillment_id(fulfillmentId).setMerchantId(merchantId).setStatus("15").call());

		log.info("moved to status 15");
	}

	public void moveToDelievered(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		FulfillmentUpdateRequest ffUpdateRequest = new FulfillmentUpdateRequest();
		responseObject.setDelliveredresponse(
				ffUpdateRequest.setFulfillment_id(fulfillmentId).setMerchantId(merchantId).setStatus("7").call());

		log.info("moved to status 7");
	}

	public void cancellation(String merchantId, String orderId, String authToken, String ssoToken,
			Map<String, String> orderDetailMap) throws Exception {

		V1CancelOrderRequest v1CancelOrderRequest = new V1CancelOrderRequest();
		responseObject.setV1CancelOrderResponse(v1CancelOrderRequest.setEid(orderDetailMap.get("eid"))
				.setEod(orderDetailMap.get("eod")).setFid(orderDetailMap.get("fid")).setSso_token(ssoToken).call());
		log.info("moved to status 6");

	}

	public void imeiUpdate(String authToken, String orderId, String merchantId) throws Exception {
		ItemsV2Request itemsV2Request = new ItemsV2Request();
		ItemsV2Response itemsDetailsResposne = itemsV2Request.setAuthtoken(authToken).setOrder_id(orderId)
				.setMerchant_id(merchantId).call();
		String item_id = itemsDetailsResposne.getData().get(0).getId().toString();
		UpdateImeiRequest request = new UpdateImeiRequest();
		UpdateImeiResponse response = request.setClient("web").setFulfillment_id(fulfillmentId).setItem_id(item_id)
				.setImei("12345678").call();

	}


}
