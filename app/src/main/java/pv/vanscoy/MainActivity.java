//Vanscoy
//F2013

//Final Exam Part A Take Home

//1. Add code to complete the Receiver by displaying the incoming sms text message and number to the user

//2. Add code to complete the Save by saving the name and number entered as a contact on the phone

//3. Add code to complete the Send to determine the status of the sms text message

//4. Add new code to listen and detect for an incoming phone call

package pv.vanscoy;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//register send sms message event
		final Button btnSend = (Button) this.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//obtain reference to the ui widgets
				final EditText number = (EditText) findViewById(R.id.txtNumber);
				final EditText text = (EditText) findViewById(R.id.txtText);
				
				/*//declare sms manager and Intents to send an sms message
				final SmsManager sms = SmsManager.getDefault();
				final PendingIntent msgSent = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(),0);
				
				//send the message
				sms.sendTextMessage(number.getText().toString(), null, text.getText().toString(), msgSent, null);*/
				Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
				
				//************************************************************************
				//add code to determine the status
				try {

				      String SENT = "sent";
				     String DELIVERED = "delivered";

				      Intent sentIntent = new Intent(SENT);
				     /*Create Pending Intents*/
				     PendingIntent sentPI = PendingIntent.getBroadcast(
				       getApplicationContext(), 0, sentIntent,
				       PendingIntent.FLAG_UPDATE_CURRENT);

				      Intent deliveryIntent = new Intent(DELIVERED);

				      PendingIntent deliverPI = PendingIntent.getBroadcast(
				       getApplicationContext(), 0, deliveryIntent,
				       PendingIntent.FLAG_UPDATE_CURRENT);
				     /* Register for SMS send action */
				     registerReceiver(new BroadcastReceiver() {

				       @Override
				      public void onReceive(Context context, Intent intent) {
				       String result = "";

				        switch (getResultCode()) {

				        case Activity.RESULT_OK:
				        result = "Transmission successful";
				        break;
				       case SmsManager.RESULT_ERROR_RADIO_OFF:
				        result = "Radio off";
				        break;
				       case SmsManager.RESULT_ERROR_NULL_PDU:
				        result = "No PDU defined";
				        break;
				       case SmsManager.RESULT_ERROR_NO_SERVICE:
				        result = "No service";
				        break;
				       }

				        Toast.makeText(getApplicationContext(), result,
				         Toast.LENGTH_LONG).show();
				      }

				      }, new IntentFilter(SENT));
				     /* Register for Delivery event */
				     registerReceiver(new BroadcastReceiver() {

				       @Override
				      public void onReceive(Context context, Intent intent) {
				       Toast.makeText(getApplicationContext(), "Delivered",
				         Toast.LENGTH_LONG).show();
				      }

				      }, new IntentFilter(DELIVERED));

				      /*Send SMS*/
				     SmsManager smsManager = SmsManager.getDefault();
				     smsManager.sendTextMessage(number.getText().toString(), null, text.getText().toString(), sentPI,
				       deliverPI);
				    } catch (Exception ex) {
				     Toast.makeText(getApplicationContext(),
				       ex.getMessage().toString(), Toast.LENGTH_LONG)
				       .show();
				     ex.printStackTrace();
				    }
				   }
			
		});

		//register remaining buttons to inner class events
		final Button btnCall = (Button) this.findViewById(R.id.btnCall);
		btnCall.setOnClickListener(new Call());
		
		final Button btnSave = (Button) this.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new Save());

		final EditText number = (EditText) findViewById(R.id.txtNumber);
		number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		//Register the activity to a BroadcastReceiver to listen for incoming SMS text messages
		this.registerReceiver(new Receiver(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}
	
	//listen for incoming text messages
	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//reference the ui widget to display incoming text
			final TextView response = (TextView) findViewById(R.id.txtResponse);
			//obtain the incoming text from the intent passed in
			final Bundle data = intent.getExtras();
			if(data != null) {
				Toast.makeText(MainActivity.this, "Message Received", Toast.LENGTH_SHORT).show();
			
				//************************************************
				//add code to display the sender and the message
				final Object[] pdusObj = (Object[]) data.get("pdus");
		         
		        for (int i = 0; i < pdusObj.length; i++) {
		             
		            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
		             
		            String senderNum = phoneNumber;
		            String message = currentMessage.getDisplayMessageBody();
		 
		            /*Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);*/
		             
		 
		           // Show alert
		            int duration = Toast.LENGTH_LONG;
		            Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, duration);
		            toast.show();
		             
		        } // end for loop
		      } // bundle is null
		}
	}

	//invoke the phone and dial a number
	class Call implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			final EditText number = (EditText) findViewById(R.id.txtNumber);
			//set up an intent to use the phone
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			//set the phone number
			callIntent.setData(Uri.parse("tel:" + number.getText().toString()));
			//launch the phone
			MainActivity.this.startActivity(callIntent);
		}
	}

	class Save implements View.OnClickListener {
		@Override
		
		public void onClick(View v) {
			final EditText name = (EditText) findViewById(R.id.txtName);
			final EditText number = (EditText) findViewById(R.id.txtNumber);
			
			//**************************************************
			//add code to save to the name and number as contact
			Intent in = new Intent(Intent.ACTION_INSERT);
			in.setType(ContactsContract.Contacts.CONTENT_TYPE);
			
			/*ContentValues val = new ContentValues();
			val.put(RawContacts.ACCOUNT_NAME, name);
			in.setType(ContactsContract.RawContacts.CONTENT_TYPE);
			*/
			in.putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString());
			in.putExtra(ContactsContract.Intents.Insert.PHONE, number.getText().toString());
			
			startActivity(in);
			
		}
	}

}
