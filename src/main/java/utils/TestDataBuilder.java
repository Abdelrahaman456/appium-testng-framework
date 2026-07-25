package utils;

public class TestDataBuilder {

    // Pre-configured default customer data
    public static final String DEFAULT_PHONE = "500421222";
    public static final String DEFAULT_OTP = "1234";
    public static final String DEFAULT_EMAIL = "aashraf@tree.com.sa";
    public static final String DEFAULT_IBAN = "SA6530400108071059170014";
    
    // Default vehicle data
    public static final String DEFAULT_SEQUENCE_NUMBER = "704848484";
    public static final String DEFAULT_CUSTOM_CARD = "1254874892";
    public static final String DEFAULT_SELLER_ID = "1313424273";
    public static final String DEFAULT_CAR_YEAR = "2026";

    // Default payment card data
    public static final String DEFAULT_CARD_NUMBER = "5123456789012346";
    public static final String DEFAULT_CARD_EXPIRY = "01/2031";
    public static final String DEFAULT_CARD_CVV = "100";
    public static final String DEFAULT_CARD_HOLDER = "Tree User";

    /**
     * Generates a dynamic unique email address per test run to prevent backend caching issues.
     */
    public static String generateUniqueEmail() {
        return "qa_test_" + System.currentTimeMillis() + "@tree.com.sa";
    }

    /**
     * Class to encapsulate credit card details.
     */
    public static class CreditCard {
        public String number;
        public String expiry;
        public String cvv;
        public String holderName;

        public CreditCard(String number, String expiry, String cvv, String holderName) {
            this.number = number;
            this.expiry = expiry;
            this.cvv = cvv;
            this.holderName = holderName;
        }

        public static CreditCard defaultCard() {
            return new CreditCard(DEFAULT_CARD_NUMBER, DEFAULT_CARD_EXPIRY, DEFAULT_CARD_CVV, DEFAULT_CARD_HOLDER);
        }
    }

    /**
     * Class to encapsulate customer profile information.
     */
    public static class CustomerProfile {
        public String nationalId;
        public String phone;
        public String email;
        public String iban;

        public CustomerProfile(String nationalId, String phone, String email, String iban) {
            this.nationalId = nationalId;
            this.phone = phone;
            this.email = email;
            this.iban = iban;
        }

        public static CustomerProfile createDefault(String nationalId) {
            return new CustomerProfile(nationalId, DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_IBAN);
        }
    }
}
