package utils;

/**
 * TestDataBuilder - now reads all values from testdata.properties via TestConfig.
 * Change values in testdata.properties WITHOUT recompiling code!
 */
public class TestDataBuilder {

    // All values loaded from testdata.properties
    public static String DEFAULT_PHONE        = TestConfig.phone();
    public static String DEFAULT_OTP          = TestConfig.otp();
    public static String DEFAULT_EMAIL        = TestConfig.email();
    public static String DEFAULT_IBAN         = TestConfig.iban();
    public static String DEFAULT_CARD_NUMBER  = TestConfig.cardNumber();
    public static String DEFAULT_CARD_EXPIRY  = TestConfig.cardExpiry();
    public static String DEFAULT_CARD_CVV     = TestConfig.cardCvv();
    public static String DEFAULT_CARD_HOLDER  = TestConfig.cardHolder();
    public static String DEFAULT_SELLER_ID    = TestConfig.sellerId();
    public static String DEFAULT_CAR_YEAR     = TestConfig.carYear();
    public static String DEFAULT_CUSTOM_CARD  = TestConfig.customCard();

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
