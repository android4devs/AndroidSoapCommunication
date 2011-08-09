package pl.froger.soapcommunication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String SERVICE_URL = "http://www.w3schools.com/webservices/tempconvert.asmx";
	private static final String SERVICE_NAMESPACE = "http://tempuri.org/";
	private static final String METHOD_CEL_TO_FAHR = "CelsiusToFahrenheit";
	private static final String METHOD_FAHR_TO_CEL = "FahrenheitToCelsius";
	private static final String SOAP_ACTION_CEL_TO_FAHR = "http://tempuri.org/CelsiusToFahrenheit";
	private static final String SOAP_ACTION_FAHR_TO_CEL = "http://tempuri.org/FahrenheitToCelsius";
	
	private EditText etConverter;
	private Button btnFahrenheitToCelsius;
	private Button btnCelsiusToFahrenheit;
	private TextView tvResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		etConverter = (EditText) findViewById(R.id.etConverter);
		btnCelsiusToFahrenheit = (Button) findViewById(R.id.btnCelsiusToFahrenheit);
		btnFahrenheitToCelsius = (Button) findViewById(R.id.btnFahrenheitToCelsius);
		tvResult = (TextView) findViewById(R.id.tvResult);
		initButtonsOnClick();
	}

	private void initButtonsOnClick() {
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btnCelsiusToFahrenheit:
					convertCelsiusToFahrenheit();
					break;
				case R.id.btnFahrenheitToCelsius:
					convertFahrenheitToCelsius();
					break;
				default:
					break;
				}
			}
		};
		btnCelsiusToFahrenheit.setOnClickListener(listener);
		btnFahrenheitToCelsius.setOnClickListener(listener);
	}

	private void convertCelsiusToFahrenheit() {
		String celsius = etConverter.getText().toString();
		String fahrenheit = convertToFahrenheit(celsius);
		setConversionResult(celsius + " Celsius degree", 
							fahrenheit + " Fahrenheit degree");
	}
	
	private String convertToFahrenheit(String celsius) {
		return callWebService(SOAP_ACTION_CEL_TO_FAHR, 
				METHOD_CEL_TO_FAHR, 
				"Celsius", 
				celsius);
	}

	private void convertFahrenheitToCelsius() {
		String fahrenheit = etConverter.getText().toString();
		String celsius = convertToCelsius(fahrenheit);
		setConversionResult(fahrenheit + " Fahrenheit degree", 
							celsius + " Celsius degree");
	}
	
	private String convertToCelsius(String fahrenheit) {
		return callWebService(SOAP_ACTION_FAHR_TO_CEL, 
				METHOD_FAHR_TO_CEL, 
				"Fahrenheit", 
				fahrenheit);
	}

	private void setConversionResult(String from, String to) {
		tvResult.setText(from +" = " + to);
	}
	
	private String callWebService(String soapAction, String method, String attribute, String val) {
		SoapObject request = prepareRequest(method, attribute, val);
		SoapSerializationEnvelope envelope = prepareSoapEnvelope(request);
		return sendRequest(soapAction, envelope);
	}

	private SoapObject prepareRequest(String method, String attribute, String val) {
		SoapObject request = new SoapObject(SERVICE_NAMESPACE, method);
		request.addProperty(attribute, val);
		return request;
	}

	private SoapSerializationEnvelope prepareSoapEnvelope(SoapObject request) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		return envelope;
	}

	private String sendRequest(String soapAction, SoapSerializationEnvelope envelope) {
		HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
		try {
			httpTransportSE.call(soapAction, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			return result.toString();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
			return "";
		}
	}
}