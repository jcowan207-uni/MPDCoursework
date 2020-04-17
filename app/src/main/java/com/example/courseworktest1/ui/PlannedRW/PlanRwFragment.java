package com.example.courseworktest1.ui.PlannedRW;
// This is the import list for the planned roadworks fragment
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.courseworktest1.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PlanRwFragment extends Fragment {//call must extend the fragment command as it splits this java class into a fragment

    private ArrayList<String> PlannedRwAL = new ArrayList<String>();

    private ListView plannedRwListView;
    private EditText plannedRwFilter;

    private ArrayAdapter<String> plannedRwAdaptor;

    //This method is for when the fragment is selected
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_current_incidents, container, false);

        //creates a new asynchronous search method
        new com.example.courseworktest1.ui.PlannedRW.PlanRwFragment.AsyncSearch().execute();

        //creates a list and edit view
        plannedRwListView = (ListView) root.findViewById(R.id.roadView);
        plannedRwFilter = (EditText) root.findViewById(R.id.Current_editText);

        //creates a filter so the data can listen to the commands
        plannedRwFilter.addTextChangedListener(new TextWatcher() {

            //what occurs before the text is changed, empty due to this being handled later on
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // this is for allowing the fragment to display the information from the dataset while being *HORIZONTAL*
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = s.toString();
                    if (!str.equals("")) {
                        (PlanRwFragment.this).plannedRwAdaptor.getFilter().filter(str);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }

    //starts a new asynchronous search method
    private class AsyncSearch extends AsyncTask<Integer, Integer, ArrayList<String>>{

        //creates an arraylist for background processes
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String PlannedRwURL = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
            try { //this whole try catch is for parsing the data from the rss feed to the app
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(PlannedRwURL);

                document.getDocumentElement().normalize();

                //new nodelist for creating a section in the apps screen for a specific piece of data parsed from the rss feed
                NodeList NL = document.getElementsByTagName("item");
                //ensures data can be shown on the screen if the feed is empty
                if (NL.getLength() >=1) {

                    //counter for data in the rss feed
                    for (int counter = 0; counter < NL.getLength(); counter++) {
                        Node PlannedRwNode = NL.item(counter);
                        if (PlannedRwNode.getNodeType() == Node.ELEMENT_NODE && PlannedRwNode != null) {

                            Element PlannedRwElement = (Element) PlannedRwNode;
                            //takes the title and description from the feed and converts them to string
                            String title, description;

                            //the next 2 if statements are for if parts of the feed are empty, this is to ensure no errors occur while parsing
                            if (PlannedRwElement.getElementsByTagName("title").item(0) != null) {
                                title = PlannedRwElement.getElementsByTagName("title").item(0).getTextContent();
                            } else {
                                title = "title not available";
                            }
                            if (PlannedRwElement.getElementsByTagName("description").item(0) != null) {
                                description = PlannedRwElement.getElementsByTagName("description").item(0).getTextContent();
                            } else {
                                description = "description not available";
                            }
                            String PlannedRwInformation = "\n" + title + "\n\n" + description + "\n";
                            //adds the next piece of parsed data into the app
                            PlannedRwAL.add(PlannedRwInformation);

                        } else { //allows for feed to be empty and not cause an error
                            PlannedRwAL.add("No incidents available to display");
                        }
                    }
                }else {
                    PlannedRwAL.add("No incidents available to display");
                }
                return PlannedRwAL;

                //this catch gives the developer an easier job of finding the error when the app is running
            } catch(Exception currentException) {
                System.out.println("AsyncError " + currentException.getMessage());
            }
            return null;
        }

        //pulls the parsed data from the feed into the app
        protected void onPostExecute(ArrayList<String> PlannedRwData) {
            try {
                plannedRwAdaptor = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        PlannedRwData

                );
                plannedRwListView.setAdapter(plannedRwAdaptor);

            } catch (Exception currentException) {
                System.out.println("error2" + currentException.getMessage());
            }
        }
        //ensures that the adaptor isn't empty and populates the listview with the adaptor data
        protected void onPreExecute() {
            try{
                plannedRwAdaptor = (ArrayAdapter<String>) plannedRwListView.getAdapter();
            } catch (Exception Exception) {
                System.out.println("error3" + Exception.getMessage());

            }
        }
    }
}
