package fr.calamus.common.mail.model;

public enum EtatsLivraisonMessage {
	messageDelivered("Messages livrés"), messageNotDelivered("Messages non livrés"), messagePartiallyDelivered("Messages partiellement livrés");
	
	private String label;

	private EtatsLivraisonMessage(String label){
		this.label=label;
	}

	public String getLabel(){
		return label;
	}
	
	
}
