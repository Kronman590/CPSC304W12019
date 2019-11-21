package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class CreditCard {
	private final String cardName;
	private final String cardNo;
	private final String ExpDate;

	public CreditCard(String cardName, String cardNo, String ExpDate) {
		this.cardName = cardName;
		this.cardNo = cardNo;
		this.ExpDate = ExpDate;
	}

	public String getCardName() {
		return cardName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public String getExpDate() {
		return ExpDate;
	}
}
