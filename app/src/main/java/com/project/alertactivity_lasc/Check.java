package com.project.alertactivity_lasc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Check extends AppCompatActivity {

    private static String Server = new Server().name();
    private static String ID;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "CheckNFC";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    ListView listView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mTextView = (TextView) findViewById(R.id.editText);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("กรุณาเปิด NFC");
        } else {
        }

        NFCIntent(getIntent());

        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        ImageView im_news = (ImageView)findViewById(R.id.imageView2);
        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText comment = (EditText)findViewById(R.id.editText);
                comment(comment.getText().toString());
                comment.setText("");
//                new onLoad().execute(Server + "register_list.php?id=" + ID);
            }
        });

//
//        ImageView im_refresh = (ImageView)findViewById(R.id.refresh);
//        im_refresh.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
////                new onLoad().execute(Server + "register_list.php?id="+ID);
//            }
//        });

//       new onLoad().execute(Server + "register_list.php?id="+ID);
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
        boolean con = false;

        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(Check.this);
            pd.setTitle("กำลังทำงาน");
            pd.setMessage("โหลดข้อมูล...");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String jString;
            HashMap<String, String> map ;
            try {
                jString = getJsonFromUrl(params[0]);

                if (jString != null) {

                    String username;
                    String dt;

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);
                        username = jObj.getString("username");
                        dt = jObj.getString("dt");

                        map = new HashMap<String, String>();
                        if( equals("2")) {
                            map.put("username1", username);
                            map.put("dt1", "วันที่ " + dt);
                            map.put("username2", "");
                            map.put("dt2", "");
                        }else{
                            map.put("username1", "");
                            map.put("dt1", "");
                            map.put("username2", username);
                            map.put("dt2", "วันที่ " + dt);
                        }
                        myList.add(map);

                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //func.MessageBox("การเชื่อมต่อผิดพลาด");
            } catch (JSONException e) {
                //func.MessageBox("การรับส่งผิดพลาด");
            }catch (Exception e) {
                //Log.d(TAG, "Problem reading " +  ex.getLocalizedMessage());
                //func.MessageBox("การเชื่อมต่อผิดพลาด");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            try{
                ListView lv = (ListView)findViewById(R.id.listView);
                ListAdapter adapter = new SimpleAdapter(getBaseContext(), myList , R.layout.list_check,
                        new String[] { "username1", "username2", "dt1", "dt2" },
                        new int[] { R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7 });
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                lv.setSelection(lv.getCount() - 1);

            }
            catch (Exception e)
            {

            }
            pd.dismiss();
            if(con==false)
            {
                MessageBox("การเชื่อมต่อผิดพลาด");
            }
        }
    }

    private String getJsonFromUrl(String strUrl) throws IOException {

        URL url = new URL(strUrl);
        try {
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            httpCon.setConnectTimeout(6*1000);
            httpCon.connect();

            int responseCode = httpCon.getResponseCode();
            //Log.d(TAG, "The response is: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK){
                //Log.d(TAG, " size: " + httpCon.getContentLength());

                InputStream ins = httpCon.getInputStream();
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(ins,"UTF-8"));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append("\n");
                    //Log.d(TAG, line);
                }
                rd.close();
                return response.toString();
            }

        } catch (Exception ex) {
            //Log.d(TAG, "Problem reading " +  ex.getLocalizedMessage());
            //ex.printStackTrace();
        }
        return null;
    }

    public void MessageBox(String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(str);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void comment(String c)
    {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK",null);

        //default Value

        String name = "";
        String major = "";
        String username = null;
        String dt = "";
        String error = "";

        String[] item ={"","","",""};
        try
        {
            String url = Server+"activity_register13.php";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", c));
            params.add(new BasicNameValuePair("activity", ID));
            String resultserver = getHttpPost(url, params);

            JSONObject jo;

                jo = new JSONObject(resultserver);
                name = jo.getString("name");
                major = jo.getString("major");
                username = jo.getString("username");
                dt = jo.getString("dt");
                error = jo.getString("error");

            item[0] = "ชื่อ : "+name;
            item[1] = major;
            item[2] = "รหัสที่เช็ค : "+username;
            item[3] = "วัน เวลาที่เช็ค : "+dt;

                Log.d("JSON Parser", "result: " + resultserver.toString());

        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e) {
//            MessageBox("การเชื่อมต่อผิดพลาด");
            MessageBox("การเชื่อมต่อผิดพลาด "+ e.toString());
        }

        if(username != null){
            dialog.setTitle("เช็คกิจกรรมเรียบร้อย");
            listView = new ListView(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, R.id.txtitem,item);
            listView.setAdapter(adapter);
            dialog.setView(listView);
            dialog.create();
            dialog.show();

        }else{
            dialog.setTitle("ผิดพลาด!!");
            dialog.setMessage(error);
            dialog.create();
            dialog.show();
        }
    }

    public String getHttpPost(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                //Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void NFCIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        NFCIntent(intent);
    }


    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at 3.2.1
			 *
			 * http://www.nfc-forum.org/specs/
			 *
			 * bit_7 defines encoding
			 * bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                comment(result);
            }
        }
    }
}
