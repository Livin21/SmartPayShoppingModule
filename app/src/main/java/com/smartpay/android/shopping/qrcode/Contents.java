package com.smartpay.android.shopping.qrcode;

import android.provider.ContactsContract;

public class Contents {
    private Contents() {
    }

    static final class Type {

        // Plain text. Use Intent.putExtra(DATA, string). This can be used for URLs too, but string
        // must include "http://" or "https://".
        static final String TEXT = "TEXT_TYPE";

        // An email type. Use Intent.putExtra(DATA, string) where string is the email address.
        static final String EMAIL = "EMAIL_TYPE";

        // Use Intent.putExtra(DATA, string) where string is the phone number to call.
        static final String PHONE = "PHONE_TYPE";

        // An SMS type. Use Intent.putExtra(DATA, string) where string is the number to SMS.
        static final String SMS = "SMS_TYPE";


        //  A contact. Send a request to encode it as follows:
        //  <p/>
        //  import android.provider.Contacts;
        //  <p/>
        //  Intent intent = new Intent(Intents.Encode.ACTION); intent.putExtra(Intents.Encode.TYPE,
        //  CONTACT); Bundle bundle = new Bundle(); bundle.putString(Contacts.Intents.Insert.NAME,
        //  "Jenny"); bundle.putString(Contacts.Intents.Insert.PHONE, "8675309");
        //  bundle.putString(Contacts.Intents.Insert.EMAIL, "jenny@the80s.com");
        //  bundle.putString(Contacts.Intents.Insert.POSTAL, "123 Fake St. San Francisco, CA 94102");
        //  intent.putExtra(Intents.Encode.DATA, bundle);

        static final String CONTACT = "CONTACT_TYPE";


        // A geographic location. Use as follows:
        // Bundle bundle = new Bundle();
        // bundle.putFloat("LAT", latitude);
        // bundle.putFloat("LONG", longitude);
        // intent.putExtra(Intents.Encode.DATA, bundle);

        static final String LOCATION = "LOCATION_TYPE";

        private Type() {
        }
    }

    static final String URL_KEY = "URL_KEY";

    static final String NOTE_KEY = "NOTE_KEY";

    // When using Type.CONTACT, these arrays provide the keys for adding or retrieving multiple
    // phone numbers and addresses.
    static final String[] PHONE_KEYS = {
            ContactsContract.Intents.Insert.PHONE, ContactsContract.Intents.Insert.SECONDARY_PHONE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE
    };

    public static final String[] PHONE_TYPE_KEYS = {
            ContactsContract.Intents.Insert.PHONE_TYPE,
            ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE
    };

    static final String[] EMAIL_KEYS = {
            ContactsContract.Intents.Insert.EMAIL, ContactsContract.Intents.Insert.SECONDARY_EMAIL,
            ContactsContract.Intents.Insert.TERTIARY_EMAIL
    };

    public static final String[] EMAIL_TYPE_KEYS = {
            ContactsContract.Intents.Insert.EMAIL_TYPE,
            ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE,
            ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE
    };
}
