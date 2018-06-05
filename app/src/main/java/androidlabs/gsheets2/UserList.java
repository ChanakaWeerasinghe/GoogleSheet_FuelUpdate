//MainActivity.java

package androidlabs.gsheets2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidlabs.gsheets2.Post.PostData;
import androidlabs.gsheets2.adapter.MyArrayAdapter;
import androidlabs.gsheets2.model.MyDataModel;
import androidlabs.gsheets2.parser.JSONParser;
import androidlabs.gsheets2.util.InternetConnection;
import androidlabs.gsheets2.util.Keys;

public class UserList extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    private String selectedCountry = null;
    private ListView listView;
    private ArrayList<MyDataModel> list;
    private ArrayList<String> list1;
    private MyArrayAdapter adapter;
    private ArrayAdapter adapter1;
    Spinner spinner;
    private Button update;
    public static TextView SelectedDateView;
    String selected,month,ids;
    ArrayAdapter<String> dataAdapter;
    String parts;
    EditText etkm;
    String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        selected=null;
        update=(Button)findViewById(R.id.update_btn1);
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<>();
        categories.add("January");
        categories.add("February");
        categories.add("March");
        categories.add("April");
        categories.add("May");
        categories.add("June");
        categories.add("July");
        categories.add("August");
        categories.add("October");
        categories.add("November");
        categories.add("December");



        etkm=(EditText)findViewById(R.id.input_country2);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                distance = etkm.getText().toString();
                if (distance.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a Your Distance", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);

                    builder
                            .setMessage("Are you Update ur Details sure?")
                            .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Yes-code

                                    Log.d("HJK",selected);
                                    String string = selected;

                                    parts = string.split("\\.")[0];
                                    Log.d("part1 part2",parts);
                                    ids= string.split("\\.")[0].toString();
                                    new UpdateDataActivity().execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }

                Log.v("EditText", etkm.getText().toString());

            }
        });





        // Creating adapter for spinner
          dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview_align);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new YourItemMonthSelectedListener() );

        /**
         * Array List for Binding Data from JSON to this List
         */
        list = new ArrayList<>();

        list1 = new ArrayList<String>();/**
         * Binding that List to Adapter
         */
        adapter = new MyArrayAdapter(this, list);

        automaticLoader();
        /**
         * Getting List and Setting List Adapter
         */
     /*   listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.parentLayout), list.get(position).getName() + " => " + list.get(position).getCountry(), Snackbar.LENGTH_LONG).show();
            }
        });*/

//        DatePickerDialog datePickerDialog =
//                new DatePickerDialog(getApplicationContext(),Listner, true, true,false);

//        SelectedDateView = (TextView) findViewById(R.id.txtMonthYear);
//

//        datePickerDialog.show();
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter1 = new ArrayAdapter<String>(this,
                R.layout.spinner_textview_align, list1);

        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new YourItemSelectedListener());


//        Spinner spinnera = (Spinner) findViewById(R.id.spinner);
////        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
////                (this, android.R.layout.simple_spinner_item,
////                        Collections.singletonList(list.toString().toString())); //selected item will look like a spinner set from XML
//        adapter.setDropDownViewResource(android.R.layout
//                .simple_spinner_dropdown_item);
//        spinnera.setAdapter(adapter);


//        spinner.getSelectedItem().toString();
//Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
        /**
         * Just to know onClick and Printing Hello Toast in Center.
         */
//        Toast toast = Toast.makeText(getApplicationContext(), "Click on FloatingActionButton to Load JSON", Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(@NonNull View view) {
//
//                Intent intent = new Intent(getApplicationContext(), PostData.class);
//                    startActivity(intent);
//            }
//        });
    }

    public void automaticLoader()
    {
        /**
         * Checking Internet Connection
         */
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new GetDataTask().execute();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Creating Get Data Task for Getting Data From Web
     */
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */

            x=list.size();

            if(x==0)
                jIndex=0;
            else
                jIndex=x;

            dialog = new ProgressDialog(UserList.this);
            dialog.setTitle("Fetching Information "  );
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = Controller.readAllData();
//Log.d("", String.valueOf(jsonObject));
            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if(jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray("records");
//                        Log.d("array", String.valueOf(array));
                        /**
                         * Check Length of Array...
                         */


                        int lenArray = array.length();
                        if(lenArray > 0) {
                            for( ; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */
                                MyDataModel model = new MyDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String name = innerObject.getString(Keys.KEY_NAME);
                                String country = innerObject.getString(Keys.KEY_COUNTRY);
//                                String image = innerObject.getString(Keys.KEY_IMAGE);
                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setName(name);
                                model.setCountry(country.trim());
  //                              model.setImage(image);

                                /**
                                 * Adding name and phone concatenation in List...
                                 */
                                list.add(model);
                                list1.add(name);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if(list.size() > 0) {
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                dataAdapter.notifyDataSetChanged();

            } else {
//                Snackbar.make(findViewById(R.id.parentLayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    public class YourItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
              selected = parent.getItemAtPosition(pos).toString();

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    public class YourItemMonthSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            month = parent.getItemAtPosition(pos).toString();
            Log.d("month",month);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


    class UpdateDataActivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        String result=null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(UserList.this);
            dialog.setTitle("Hey Wait Please..."+x);
            dialog.setMessage("I am getting your JSON");
            dialog.show();

        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = Controller.updateData(ids,distance,month);
            Log.i(Controller.TAG, "Json obj ");

            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {

                    result=jsonObject.getString("result");

                }
            } catch (JSONException je) {
                Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }



}