/*
 * MIT License
 *
 * Copyright (c) 2018.  Livin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.smartpay.android.payment.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.nfc.tech.Ndef
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.lmntrx.android.library.livin.missme.ProgressDialog
import com.smartpay.android.R
import com.smartpay.android.payment.Wallet
import com.smartpay.android.payment.WalletActivity
import com.smartpay.android.payment.qrcode.WalletAddressScannerActivity
import com.smartpay.android.shopping.activity.MainActivity

class BeamPayActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback {

    private var mNfcAdapter: NfcAdapter? = null

    private var billAmount = 0.0


    private var mPendingIntent: PendingIntent? = null

    private val mTechLists = arrayOf(
            arrayOf(Ndef::class.java.name)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_grabber)

        billAmount = intent.getDoubleExtra("BILL_AMOUNT", 0.0)


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC. Switching to QR Cde mode.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, WalletAddressScannerActivity::class.java)
            intent.putExtra("BILL_AMOUNT", billAmount)
            startActivity(intent)
            finish()
            return
        }

        val intent = Intent(this, BeamPayActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        mNfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, mTechLists)


        if (!mNfcAdapter!!.isEnabled) {
            findViewById<TextView>(R.id.errorText).setText(R.string.nfc_disabled)
        }


        mNfcAdapter?.setNdefPushMessageCallback(this@BeamPayActivity, this@BeamPayActivity)



        findViewById<Button>(R.id.switchModeButton).setOnClickListener {
            val i = Intent(this@BeamPayActivity, WalletActivity::class.java)
            startActivity(i)
            finish()
        }

    }



    override fun createNdefMessage(event: NfcEvent?): NdefMessage {

        return NdefMessage(arrayOf(
                createMime("application/vnd.com.lmntrx.android.smartpaywallet.payment.nfc", "Got it".toByteArray())
                ,NdefRecord.createApplicationRecord("com.lmntrx.android.smartpaywallet")

        ))

    }

    override fun onResume() {
        super.onResume()
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // onResume gets called after this to handle the intent
        setIntent(intent)
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    fun processIntent(intent: Intent) {
        val rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES)
        // only one message sent during the beam
        val msg = rawMsgs[0] as NdefMessage

        val address = String(msg.records[0].payload)
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Toast.makeText(this, address, Toast.LENGTH_LONG).show()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing Payment. Please Wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        Wallet.getWallet(this, object : Wallet.OnWalletFetchCompletedListener {
            override fun onComplete(wallet: Wallet) {
                if (wallet.balance >= billAmount)
                    wallet.addTransaction(this@BeamPayActivity, address, billAmount, object : Wallet.OnTransactionCompleteListener {
                        override fun onComplete() {
                            progressDialog.dismiss()
                            Toast.makeText(this@BeamPayActivity, "Payment Complete", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@BeamPayActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onError(error: String) {
                            Toast.makeText(this@BeamPayActivity, error, Toast.LENGTH_SHORT).show()
                        }
                    })
                else {
                    progressDialog.dismiss()
                    Toast.makeText(this@BeamPayActivity, "You don't have enough credits to complete this purchase. Please recharge your wallet.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@BeamPayActivity, WalletActivity::class.java))
                    finish()
                }
            }

            override fun onError(s: String) {
                Toast.makeText(this@BeamPayActivity, s, Toast.LENGTH_SHORT).show()
            }
        })

    }
}
